import { BoardTask } from "../types";
import { Avatar, Card, CardContent, Stack, Typography } from "@mui/material";
import { stringToColor } from "@/lib/utils.ts";

interface BoardTaskCardProps {
  boardTask: BoardTask;
  onClick: (task: BoardTask) => void;
}

export const BoardTaskCard = ({ boardTask, onClick }: BoardTaskCardProps) => {
  const handleClick = () => {
    onClick(boardTask);
  };
  const assigneeAvatars = boardTask.assignees.map((assignee) => (
    <Avatar
      key={assignee.userId}
      sx={{ width: 32, height: 32, fontSize: "1rem", bgcolor: stringToColor(assignee.userId) }}
    >
      {assignee.firstName[0]}
      {assignee.lastName[0]}
    </Avatar>
  ));
  return (
    <Card onClick={handleClick} sx={{ "&:hover": { cursor: "pointer" } }}>
      <CardContent>
        <Typography mb={1}>{boardTask.title}</Typography>
        <Stack direction="row" spacing={1}>
          {assigneeAvatars}
        </Stack>
      </CardContent>
    </Card>
  );
};
