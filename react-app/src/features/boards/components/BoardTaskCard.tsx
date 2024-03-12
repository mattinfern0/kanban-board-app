import { BoardTask } from "../types";
import { Card, CardContent, Typography } from "@mui/material";

interface BoardTaskCardProps {
  boardTask: BoardTask;
}

export const BoardTaskCard = ({ boardTask }: BoardTaskCardProps) => {
  return (
    <Card>
      <CardContent>
        <Typography>{boardTask.title}</Typography>
      </CardContent>
    </Card>
  );
};
