import { BoardTask } from "../types";
import { Avatar, Card, Group, Stack, Text } from "@mantine/core";
import { TaskPriorityIcon } from "@/features/tasks/components/TaskPriorityIcon.tsx";

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
      <Stack>
        <Group align="center">
          {boardTask.priority && <TaskPriorityIcon priority={boardTask.priority} />}
          <Text size="lg">{boardTask.title}</Text>
        </Group>

        <Avatar.Group>
          {assigneeAvatars}
          {numberExtraAssignees > 0 && <Avatar color="gray">+{numberExtraAssignees}</Avatar>}
        </Avatar.Group>
      </Stack>
    </Card>
  );
};
