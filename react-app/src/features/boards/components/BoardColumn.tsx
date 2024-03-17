import { Card, CardContent, Stack, Typography } from "@mui/material";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType, BoardTask } from "../types";

interface BoardColumnProps {
  boardColumn: BoardColumnType;
  onTaskCardClick: (task: BoardTask) => void;
}

export const BoardColumn = ({ boardColumn, onTaskCardClick }: BoardColumnProps) => {
  const cardElements = boardColumn.tasks.map((t) => {
    return <BoardTaskCard onClick={onTaskCardClick} key={t.id} boardTask={t} />;
  });

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent sx={{ height: "100%" }}>
        <Typography variant="body1" mb={1}>
          {boardColumn.title}
        </Typography>
        <Stack spacing={3} sx={{ backgroundColor: "lightgray", padding: "3px" }}>
          {cardElements}
        </Stack>
      </CardContent>
    </Card>
  );
};
