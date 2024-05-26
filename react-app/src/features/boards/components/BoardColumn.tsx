import { Card, CardContent, Stack, Typography } from "@mui/material";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType, BoardTask } from "../types";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { SortableDraggable } from "@/components/dragging/SortableDraggable.tsx";
import { useDroppable } from "@dnd-kit/core";
import { DragHandle } from "@mui/icons-material";

interface BoardColumnProps {
  boardColumn: BoardColumnType;
  onTaskCardClick: (task: BoardTask) => void;
}

export const BoardColumn = ({ boardColumn, onTaskCardClick }: BoardColumnProps) => {
  const { setNodeRef } = useDroppable({ id: boardColumn.id });

  const cardElements = boardColumn.tasks.map((t) => {
    return (
      <SortableDraggable key={t.id} id={t.id}>
        <BoardTaskCard onClick={onTaskCardClick} boardTask={t} />
      </SortableDraggable>
    );
  });

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent sx={{ height: "100%" }}>
        <Stack direction="row" justifyContent="space-between" mb={1}>
          <Typography variant="h6">{boardColumn.title}</Typography>
          <DragHandle />
        </Stack>

        <SortableContext id={boardColumn.id} items={boardColumn.tasks} strategy={verticalListSortingStrategy}>
          <Stack
            ref={setNodeRef}
            sx={{ height: "60vh", backgroundColor: "lightgray", padding: "3px", overflowY: "scroll" }}
            spacing={3}
          >
            {cardElements}
          </Stack>
        </SortableContext>
      </CardContent>
    </Card>
  );
};
