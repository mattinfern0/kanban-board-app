import { TaskStatus } from "@/types";
import { Chip } from "@mui/material";
import { OverridableStringUnion } from "@mui/types";
import { ChipPropsColorOverrides } from "@mui/material/Chip/Chip";

type ChipColor = OverridableStringUnion<
  "default" | "primary" | "secondary" | "error" | "info" | "success" | "warning",
  ChipPropsColorOverrides
>;

const statusToColor: Record<TaskStatus, ChipColor> = {
  BACKLOG: "secondary",
  COMPLETED: "success",
  IN_PROGRESS: "primary",
  OTHER: "secondary",
  TODO: "secondary",
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

  return <Chip label={statusToLabel[status]} variant="filled" color={statusToColor[status]} />;
};
