import { BoardTask } from "../types";
import { Avatar, Card, Group, Stack, Text, useMantineTheme } from "@mantine/core";
import { TaskPriorityIcon } from "@/features/tasks/components/TaskPriorityIcon.tsx";

interface BoardTaskCardProps {
  boardTask: BoardTask;
  onClick: (task: BoardTask) => void;
}

export const BoardTaskCard = ({ boardTask, onClick }: BoardTaskCardProps) => {
  const theme = useMantineTheme();
  const handleClick = () => {
    onClick(boardTask);
  };
  const visibleAssignees = boardTask.assignees.slice(0, 3);
  const assigneeAvatars = visibleAssignees.map((assignee) => (
    <Avatar key={assignee.userId} name={`${assignee.firstName} ${assignee.lastName}`} color="initials" />
  ));

  const numberExtraAssignees = boardTask.assignees.length - visibleAssignees.length;
  return (
    <Card
      onClick={handleClick}
      shadow="paper"
      style={{
        cursor: "pointer",
        backgroundColor: theme.colors.yellow[0],
      }}
    >
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
