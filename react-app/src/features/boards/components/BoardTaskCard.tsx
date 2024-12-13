import { BoardTask } from "../types";
import { Avatar, Card, Text } from "@mantine/core";

interface BoardTaskCardProps {
  boardTask: BoardTask;
  onClick: (task: BoardTask) => void;
}

export const BoardTaskCard = ({ boardTask, onClick }: BoardTaskCardProps) => {
  const handleClick = () => {
    onClick(boardTask);
  };
  const visibleAssignees = boardTask.assignees.slice(0, 3);
  const assigneeAvatars = visibleAssignees.map((assignee) => (
    <Avatar key={assignee.userId} name={`${assignee.firstName} ${assignee.lastName}`} color="initials"></Avatar>
  ));

  const numberExtraAssignees = boardTask.assignees.length - visibleAssignees.length;
  return (
    <Card onClick={handleClick} withBorder shadow="sm" style={{ cursor: "pointer" }}>
      <Text size="lg" mb="1rem">
        {boardTask.title}
      </Text>
      <Avatar.Group>
        {assigneeAvatars}
        {numberExtraAssignees > 0 && <Avatar color="gray">+{numberExtraAssignees}</Avatar>}
      </Avatar.Group>
    </Card>
  );
};
