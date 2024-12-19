import { Group, Select, SelectProps } from "@mantine/core";
import React from "react";
import { TaskPriority } from "@/features/tasks/types";
import { TaskPriorityIcon } from "@/features/tasks/components/TaskPriorityIcon.tsx";

interface Props extends Omit<SelectProps, "data"> {}

interface TaskPriorityOption {
  value: TaskPriority;
  label: string;
}

const selectOptions: TaskPriorityOption[] = [
  { value: "LOW", label: "Low" },
  { value: "MEDIUM", label: "Medium" },
  { value: "HIGH", label: "High" },
];

export const TaskPrioritySelect = React.forwardRef<HTMLInputElement, Props>((props, ref) => {
  const { value } = props;

  const renderSelectOption: SelectProps["renderOption"] = ({ option }) => (
    <Group flex={1} gap="xs">
      <TaskPriorityIcon priority={option.value as TaskPriority} />
      {option.label}
    </Group>
  );
  return (
    <Select
      ref={ref}
      data={selectOptions}
      {...props}
      renderOption={renderSelectOption}
      leftSection={value ? <TaskPriorityIcon priority={value as TaskPriority} /> : null}
      clearable
    />
  );
});
