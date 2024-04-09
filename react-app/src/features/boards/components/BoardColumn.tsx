import { Card, CardContent, Stack, Typography } from "@mui/material";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType, BoardTask } from "../types";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { SortableDraggable } from "@/components/dragging/SortableDraggable.tsx";

interface BoardColumnProps {
  boardColumn: BoardColumnType;
  onTaskCardClick: (task: BoardTask) => void;
}

export const BoardColumn = ({ boardColumn, onTaskCardClick }: BoardColumnProps) => {
  const cardElements = boardColumn.tasks.map((t) => {
    return (
      <SortableDraggable key={t.id} id={t.id}>
        <BoardTaskCard onClick={onTaskCardClick} boardTask={t} />
      </SortableDraggable>
    );
  });

  const taskSortItems: string[] = boardColumn.tasks.map((t) => t.id);

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent sx={{ height: "100%" }}>
        <Typography variant="body1" mb={1}>
          {boardColumn.title}
        </Typography>
        <SortableContext items={taskSortItems} strategy={verticalListSortingStrategy}>
          <Stack spacing={3} sx={{ backgroundColor: "lightgray", padding: "3px" }}>
            {cardElements}
          </Stack>
        </SortableContext>
      </CardContent>
    </Card>
  );
};
