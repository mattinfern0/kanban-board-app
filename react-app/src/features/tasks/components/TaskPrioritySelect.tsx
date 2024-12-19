import { Select, SelectProps } from "@mantine/core";
import React from "react";
import { TaskPriority } from "@/features/tasks/types";

interface Props extends Omit<SelectProps, "data"> {}

const selectOptions: { value: TaskPriority; label: string }[] = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" },
];

export const TaskPrioritySelect = React.forwardRef<HTMLInputElement, Props>((props, ref) => {
  return <Select ref={ref} data={selectOptions} {...props} />;
});
