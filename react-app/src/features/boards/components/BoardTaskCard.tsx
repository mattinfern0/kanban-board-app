import { BoardTask } from "../types";
import { Avatar, Card, Group, Text } from "@mantine/core";

interface BoardTaskCardProps {
  boardTask: BoardTask;
  onClick: (task: BoardTask) => void;
}

export const BoardTaskCard = ({ boardTask, onClick }: BoardTaskCardProps) => {
  const handleClick = () => {
    onClick(boardTask);
  };
  const assigneeAvatars = boardTask.assignees.map((assignee) => (
    <Avatar key={assignee.userId} name={`${assignee.firstName} ${assignee.lastName}`} color="initials"></Avatar>
  ));
  return (
    <Card onClick={handleClick} withBorder shadow="sm" style={{ cursor: "pointer" }}>
      <Text size="lg" mb="1rem">
        {boardTask.title}
      </Text>
      <Group>{assigneeAvatars}</Group>
    </Card>
  );
};
