import { Button, Card, CardContent, Stack, Typography } from "@mui/material";
import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType } from "../types";
import { Add } from "@mui/icons-material";

interface BoardColumnProps {
  boardColumn: BoardColumnType;
}

export const BoardColumn = ({ boardColumn }: BoardColumnProps) => {
  const cardElements = boardColumn.tasks.map((t) => {
    return <BoardTaskCard key={t.id} boardTask={t} />;
  });

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent sx={{ height: "100%" }}>
        <Typography variant="body1" mb={1}>
          {boardColumn.title}
        </Typography>
        <Stack spacing={3} sx={{ backgroundColor: "lightgray", padding: "3px" }}>
          {cardElements}
          <Card>
            <CardContent>
              <Button startIcon={<Add />} color="secondary" variant="contained" fullWidth size="large">
                Add
              </Button>
            </CardContent>
          </Card>
        </Stack>
      </CardContent>
    </Card>
  );
};
