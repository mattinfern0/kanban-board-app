import { Title } from "@mantine/core";
import { TaskListTable } from "@/features/tasks/components/TaskListTable.tsx";

export const TaskListView = () => {
  return (
    <>
      <Title order={1}>Tasks</Title>
      <p>Whoa whoa you're here too early! Come back when this page is done!</p>
      <TaskListTable />
    </>
  );
};
