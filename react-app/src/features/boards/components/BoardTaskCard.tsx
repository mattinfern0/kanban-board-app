import { BoardTask } from "../types";
import { Card, CardContent, Typography } from "@mui/material";

interface BoardTaskCardProps {
  boardTask: BoardTask;
  onClick: (task: BoardTask) => void;
}

export const BoardTaskCard = ({ boardTask, onClick }: BoardTaskCardProps) => {
  const handleClick = () => {
    onClick(boardTask);
  };
  return (
    <Card onClick={handleClick} sx={{ "&:hover": { cursor: "pointer" } }}>
      <CardContent>
        <Typography>{boardTask.title}</Typography>
      </CardContent>
    </Card>
  );
};
