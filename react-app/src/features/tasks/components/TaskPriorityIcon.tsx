import { TaskPriority } from "@/features/tasks/types";
import { IconExclamationCircleFilled } from "@tabler/icons-react";
import React from "react";

const priorityColors: Record<TaskPriority, string> = {
  LOW: "blue",
  MEDIUM: "orange",
  HIGH: "red",
};

interface Props extends Omit<React.ComponentProps<typeof IconExclamationCircleFilled>, "color"> {
  priority: TaskPriority;
}

export const TaskPriorityIcon = ({ priority, ...rest }: Readonly<Props>) => {
  return <IconExclamationCircleFilled {...rest} color={priorityColors[priority]} />;
};
