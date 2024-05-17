import { useBoardQuery } from "../apis/getBoard.ts";
import { Button, Grid, Stack, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";
import { useState } from "react";
import { Board, BoardColumn as BoardColumnType, BoardTask } from "@/features/boards/types";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";
import { CreateTaskDialog } from "@/features/tasks/components/CreateTaskDialog.tsx";
import {
  closestCenter,
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

  const gridColumnSize = 12 / board.boardColumns.length;
  const columnElements = board.boardColumns.map((c) => (
    <Grid key={c.id} item md={gridColumnSize}>
      <BoardColumn boardColumn={c} onTaskCardClick={handleTaskCardClick} />
    </Grid>
  ));

  const taskIdToTasks: Record<string, BoardTask> = {};
  const taskIdToBoardColumn: Record<string, BoardColumnType> = {};

  for (const column of board.boardColumns) {
    for (const task of column.tasks) {
      taskIdToTasks[task.id] = task;
      taskIdToBoardColumn[task.id] = column;
    }
  }

  console.log(draggingTaskId);

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    setDraggingTaskId(active.id);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    console.log(event);
    const { active, over } = event;

    console.debug(active, over);

    if (over != null && Object.keys(taskIdToTasks).includes(String(over.id))) {
      if (updateTaskColumnPositionMutation.isPending) {
        return;
      }

      console.log("Task dropped on task droppable");
      const overBoardColumn = taskIdToBoardColumn[over.id];
      const newIndex = overBoardColumn.tasks.findIndex((t) => t.id === over.id);

      updateTaskColumnPositionMutation.mutate(
        {
          taskId: active.id as string,
          body: { boardColumnId: overBoardColumn.id, orderIndex: newIndex },
        },
        {
          onError: () => {
            enqueueSnackbar("Failed to move task.", { variant: "error" });
          },
        },
      );
    } else {
      console.log("Task dropped on a column droppable");
    }
    setDraggingTaskId(null);
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
    >
      <Grid container spacing={3}>
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
        <Typography variant="h5" mb={0}>
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
