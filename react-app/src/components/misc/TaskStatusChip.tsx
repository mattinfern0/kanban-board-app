import { TaskStatus } from "@/types";
import { Badge } from "@mantine/core";

const statusToColor: Record<TaskStatus, string> = {
  BACKLOG: "gray",
  COMPLETED: "green",
  IN_PROGRESS: "blue",
  OTHER: "gray",
  TODO: "gray",
};

const statusToLabel: Record<TaskStatus, string> = {
  BACKLOG: "Backlog",
  COMPLETED: "Completed",
  IN_PROGRESS: "In Progress",
  OTHER: "Other",
  TODO: "To Do",
};

export const TaskStatusChip = (props: { status: TaskStatus }) => {
  const { status } = props;

  return (
    <Badge variant="filled" color={statusToColor[status]} size="xl" radius="sm">
      {statusToLabel[status]}
    </Badge>
  );
};
