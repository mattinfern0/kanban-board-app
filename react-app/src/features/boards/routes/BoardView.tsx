import { useBoardQuery } from "../apis/getBoard.ts";
import { Button, Grid, IconButton, Stack, Typography } from "@mui/material";
import { Link, useParams } from "react-router-dom";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";
import { useEffect, useState } from "react";
import { BoardColumn as BoardColumnType, BoardDetail, BoardTask } from "@/features/boards/types";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";
import { CreateTaskDialog } from "@/features/tasks/components/CreateTaskDialog.tsx";
import {
  Active,
  closestCorners,
  DndContext,
  DragEndEvent,
  DragOverEvent,
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
import { Settings } from "@mui/icons-material";

interface BoardColumnsProps {
  board: BoardDetail;
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

  const getContainingBoardColumn = (dragElementId: UniqueIdentifier): BoardColumnType | undefined => {
    if (dragElementId == undefined) {
      return undefined;
    }
    return taskIdToBoardColumn[dragElementId] || board.boardColumns.find((c) => c.id === dragElementId);
  };

  const isDragElementTask = (active: Active): boolean => {
    return typeof active.id === "string" && Object.keys(taskIdToTasks).includes(active.id);
  };

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;

    if (isDragElementTask(active) && !updateTaskColumnPositionMutation.isPending) {
      setDraggingTaskId(active.id);
    }
  };

  const handleDragTaskOver = (event: DragOverEvent) => {
    const { active, over } = event;
    if (!over || typeof active.id !== "string" || updateTaskColumnPositionMutation.isPending) {
      return;
    }

    const originalColumn = getContainingBoardColumn(active.id);
    const overColumn = getContainingBoardColumn(over.id);

    // If the task is not being dragged over to a different column, do nothing
    if (!originalColumn || !overColumn || originalColumn.id === overColumn.id) {
      return;
    }

    // If the task is being dragged over to a different column, remove the task from the active column
    // and make it appear in the correct position in the over column. This will only affect the UI state (localBoardColumn)
    // and won't be persisted to the backend until the task is dropped.
    setLocalBoardColumns((oldData) => {
      const newData = structuredClone(oldData);

      const newDataOriginalColumn = newData.find((c) => c.id === originalColumn.id);
      if (newDataOriginalColumn != null) {
        newDataOriginalColumn.tasks = newDataOriginalColumn.tasks.filter((t) => t.id !== active.id);
      }

      const newDataOverColumn = newData.find((c) => c.id === overColumn.id);
      if (newDataOverColumn != null) {
        const overIndex = newDataOverColumn.tasks.findIndex((t) => t.id === over.id);
        newDataOverColumn.tasks.splice(overIndex, 0, taskIdToTasks[active.id]);
      }

      return newData;
    });
  };

  const handleDragOver = (event: DragEndEvent) => {
    const { active } = event;

    if (isDragElementTask(active)) {
      handleDragTaskOver(event);
    }
  };

  const handleTaskDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    setDraggingTaskId(null);

    if (!over || typeof active.id !== "string" || updateTaskColumnPositionMutation.isPending) {
      return;
    }

    const originalColumn = getContainingBoardColumn(active.id);
    const overColumn = getContainingBoardColumn(over.id);

    // If the columns are different, do nothing
    if (!originalColumn || !overColumn || originalColumn.id !== overColumn.id) {
      return;
    }

    const activeIndex = overColumn.tasks.findIndex((task) => task.id === active.id);
    const overIndex = overColumn.tasks.findIndex((task) => task.id === over.id);

    if (activeIndex !== overIndex) {
      const oldLocalBoardColumns = localBoardColumns;

      setLocalBoardColumns((oldData) => {
        const newData = structuredClone(oldData);

        const newDataOverColumn = newData.find((c) => c.id === overColumn.id);
        if (newDataOverColumn != null) {
          newDataOverColumn.tasks = arrayMove(newDataOverColumn.tasks, activeIndex, overIndex);
        }

        return newData;
      });

      updateTaskColumnPositionMutation.mutate(
        {
          taskId: active.id,
          body: { boardColumnId: overColumn.id, orderIndex: overIndex },
        },
        {
          onError: () => {
            enqueueSnackbar("Failed to move task.", { variant: "error" });
            setLocalBoardColumns(oldLocalBoardColumns);
          },
        },
      );
    }
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active } = event;

    if (isDragElementTask(active)) {
      handleTaskDragEnd(event);
    }
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

        <Stack direction="row" spacing={3}>
          <Button variant="contained" onClick={() => setShowCreateTaskDialog(true)}>
            Create Task
          </Button>
          <IconButton component={Link} to={`/boards/${boardId}/settings`}>
            <Settings />
          </IconButton>
        </Stack>
      </Stack>

      <BoardColumns board={board} handleTaskCardClick={onTaskCardClick} />
    </>
  );
};
