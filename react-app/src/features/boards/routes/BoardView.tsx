import { useBoardQuery } from "../apis/getBoard.ts";
import { Button, Grid, Stack, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";
import { useEffect, useState } from "react";
import { Board, BoardColumn as BoardColumnType, BoardTask } from "@/features/boards/types";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";
import { CreateTaskDialog } from "@/features/tasks/components/CreateTaskDialog.tsx";
import {
  closestCorners,
  DndContext,
  DragEndEvent,
  DragOverlay,
  DragStartEvent,
  PointerSensor,
  UniqueIdentifier,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { useUpdateTaskColumnPositionMutation } from "@/features/tasks/apis/updateTaskColumnPosition.ts";
import { useSnackbar } from "notistack";
import { usePrevious } from "@/lib/hooks.ts";
import deepEqual from "deep-equal";
import { arrayMove } from "@dnd-kit/sortable";

interface BoardColumnsProps {
  board: Board;
  handleTaskCardClick: (task: BoardTask) => void;
}

const BoardColumns = (props: BoardColumnsProps) => {
  const { board, handleTaskCardClick } = props;
  const [draggingTaskId, setDraggingTaskId] = useState<UniqueIdentifier | null>(null);
  const { enqueueSnackbar } = useSnackbar();
  const sensors = useSensors(
    useSensor(PointerSensor, {
      // Differentiate between dragging a task vs. clicking to open the task detail dialog
      activationConstraint: {
        distance: 8,
      },
    }),
  );
  const updateTaskColumnPositionMutation = useUpdateTaskColumnPositionMutation();

  const [localBoardColumns, setLocalBoardColumns] = useState<BoardColumnType[]>(board.boardColumns);

  const previousBoardColumns = usePrevious(board.boardColumns);
  useEffect(() => {
    if (deepEqual(board.boardColumns, previousBoardColumns)) {
      return;
    }
    setLocalBoardColumns(board.boardColumns);
  }, [board.boardColumns, previousBoardColumns]);

  const gridColumnSize = 12 / localBoardColumns.length;
  const columnElements = localBoardColumns.map((c) => (
    <Grid key={c.id} item md={gridColumnSize}>
      <BoardColumn boardColumn={c} onTaskCardClick={handleTaskCardClick} />
    </Grid>
  ));

  const taskIdToTasks: Record<string, BoardTask> = {};
  const taskIdToBoardColumn: Record<string, BoardColumnType> = {};

  for (const column of localBoardColumns) {
    for (const task of column.tasks) {
      taskIdToTasks[task.id] = task;
      taskIdToBoardColumn[task.id] = column;
    }
  }

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    setDraggingTaskId(active.id);
  };

  const handleDragOver = (event: DragEndEvent) => {
    const { active, over } = event;

    const activeBoardColumn = taskIdToBoardColumn[active.id as string];
    const overContainingBoardColumn = taskIdToBoardColumn[over?.id as string];

    // If the task is not being dragged over to a different column, do nothing
    if (!activeBoardColumn || !overContainingBoardColumn || activeBoardColumn.id === overContainingBoardColumn.id) {
      return;
    }

    // If the task is being dragged over to a different column, remove the task from the active column
    // and make it appear in the correct position in the over column. This will only affect the UI state (localBoardColumn)
    // and won't be persisted to the backend until the task is dropped.
    setLocalBoardColumns((oldData) => {
      const newData = structuredClone(oldData);

      const newDataActiveColumn = newData.find((c) => c.id === activeBoardColumn.id);
      if (newDataActiveColumn != null) {
        newDataActiveColumn.tasks = newDataActiveColumn.tasks.filter((t) => t.id !== active.id);
      }

      const newDataOverColumn = newData.find((c) => c.id === overContainingBoardColumn.id);
      if (newDataOverColumn != null) {
        const overIndex = newDataOverColumn.tasks.findIndex((t) => t.id === over?.id);
        newDataOverColumn.tasks.splice(overIndex, 0, taskIdToTasks[active.id as string]);
      }

      return newData;
    });
  };

  const handleDragEnd = (event: DragEndEvent) => {
    console.log(event);
    const { active, over } = event;

    console.debug(active, over);

    const activeBoardColumn = taskIdToBoardColumn[active.id as string];
    const overBoardColumn = taskIdToBoardColumn[over?.id as string];

    if (!activeBoardColumn || !overBoardColumn || activeBoardColumn.id !== overBoardColumn.id) {
      return;
    }

    const activeIndex = activeBoardColumn.tasks.findIndex((t) => t.id === active.id);
    const overIndex = overBoardColumn.tasks.findIndex((t) => t.id === over?.id);

    if (activeIndex !== overIndex && !updateTaskColumnPositionMutation.isPending) {
      const oldLocalBoardColumns = localBoardColumns;

      setLocalBoardColumns((oldData) => {
        const newData = structuredClone(oldData);

        const newDataOverColumn = newData.find((c) => c.id === overBoardColumn.id);
        if (newDataOverColumn != null) {
          newDataOverColumn.tasks = arrayMove(newDataOverColumn.tasks, activeIndex, overIndex);
        }

        return newData;
      });

      updateTaskColumnPositionMutation.mutate(
        {
          taskId: active.id as string,
          body: { boardColumnId: overBoardColumn.id, orderIndex: overIndex },
        },
        {
          onError: () => {
            enqueueSnackbar("Failed to move task.", { variant: "error" });
            setLocalBoardColumns(oldLocalBoardColumns);
          },
        },
      );
    }

    setDraggingTaskId(null);
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <Grid container spacing={3} height="75vh">
        {columnElements}
      </Grid>
      <DragOverlay>
        {draggingTaskId ? <BoardTaskCard boardTask={taskIdToTasks[draggingTaskId]} onClick={() => {}} /> : null}
      </DragOverlay>
    </DndContext>
  );
};

export const BoardView = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [showTaskDialog, setShowTaskDialog] = useState<boolean>(false);
  const [taskDialogTaskId, setTaskDialogTaskId] = useState<string | null>(null);
  const [showCreateTaskDialog, setShowCreateTaskDialog] = useState<boolean>(false);

  if (boardQuery.isPending) {
    return <Typography>Loading...</Typography>;
  }

  if (boardQuery.isError) {
    console.error(boardQuery.error);
    return <Typography>Error!</Typography>;
  }

  const board = boardQuery.data;

  const onTaskCardClick = (task: BoardTask) => {
    setTaskDialogTaskId(task.id);
    setShowTaskDialog(true);
  };

  return (
    <>
      <BoardTaskDetail
        open={showTaskDialog}
        taskId={taskDialogTaskId}
        organizationId={board.organizationId}
        onClose={() => {
          setShowTaskDialog(false);
        }}
      />
      <CreateTaskDialog
        open={showCreateTaskDialog}
        onClose={() => setShowCreateTaskDialog(false)}
        organizationId={board.organizationId}
        boardId={board.id}
      />

      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" mb={0}>
          {boardQuery.data.title}
        </Typography>
        <Button variant="contained" onClick={() => setShowCreateTaskDialog(true)}>
          Create Task
        </Button>
      </Stack>

      <BoardColumns board={board} handleTaskCardClick={onTaskCardClick} />
    </>
  );
};
