import { BoardColumn as BoardColumnType, BoardDetail, BoardTask } from "@/features/boards/types";
import React, { useEffect, useState } from "react";
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
import { useSnackbar } from "notistack";
import { useUpdateTaskColumnPositionMutation } from "@/features/tasks/apis/updateTaskColumnPosition.ts";
import { usePrevious } from "@/lib/hooks.ts";
import deepEqual from "deep-equal";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";
import { arrayMove } from "@dnd-kit/sortable";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { Grid } from "@mantine/core";

interface BoardColumnWorkspaceProps {
  board: BoardDetail;
  handleTaskCardClick: (task: BoardTask) => void;
}

interface UseBoardColumnWorkspaceReturn {
  draggingTask: BoardTask | null;
  handleDragStart: (event: DragStartEvent) => void;
  handleDragOver: (event: DragOverEvent) => void;
  handleDragEnd: (event: DragEndEvent) => void;
  boardColumns: BoardColumnType[];
}

const useBoardColumnWorkspace = (board: BoardDetail): UseBoardColumnWorkspaceReturn => {
  const [draggingTaskId, setDraggingTaskId] = useState<UniqueIdentifier | null>(null);
  const { enqueueSnackbar } = useSnackbar();
  const updateTaskColumnPositionMutation = useUpdateTaskColumnPositionMutation();

  // Need to have a separate local state to make the DND UI work properly
  const [localBoardColumns, setLocalBoardColumns] = useState<BoardColumnType[]>(board.boardColumns);

  const previousBoardColumns = usePrevious(board.boardColumns);
  useEffect(() => {
    if (deepEqual(board.boardColumns, previousBoardColumns)) {
      return;
    }
    setLocalBoardColumns(board.boardColumns);
  }, [board.boardColumns, previousBoardColumns]);

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
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active } = event;

    if (isDragElementTask(active)) {
      handleTaskDragEnd(event);
    }
  };

  return {
    boardColumns: localBoardColumns,

    draggingTask: (draggingTaskId ? taskIdToTasks[draggingTaskId] : null) || null,
    handleDragStart,
    handleDragOver,
    handleDragEnd,
  };
};

export const BoardColumnWorkspace = (props: BoardColumnWorkspaceProps) => {
  const { board, handleTaskCardClick } = props;
  const { draggingTask, handleDragStart, handleDragOver, handleDragEnd, boardColumns } = useBoardColumnWorkspace(board);
  const sensors = useSensors(
    useSensor(PointerSensor, {
      // Differentiate between dragging a task vs. clicking to open the task detail dialog
      activationConstraint: {
        distance: 8,
      },
    }),
  );

  const gridColumnSize = 12 / boardColumns.length;
  const columnElements = boardColumns.map((c) => (
    <Grid.Col key={c.id} span={gridColumnSize}>
      <BoardColumn boardColumn={c} onTaskCardClick={handleTaskCardClick} />
    </Grid.Col>
  ));

  let dragOverlayElement: React.ReactNode | null = null;
  if (draggingTask) {
    dragOverlayElement = <BoardTaskCard boardTask={draggingTask} onClick={() => {}} />;
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <Grid>{columnElements}</Grid>
      <DragOverlay>{dragOverlayElement}</DragOverlay>
    </DndContext>
  );
};
