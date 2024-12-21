import { Stack, Title } from "@mantine/core";
import { TaskListTable } from "@/features/tasks/components/TaskListTable.tsx";
import { useState } from "react";
import { TaskDetailModal } from "@/features/tasks/components/TaskDetailModal.tsx";

export const TaskListView = () => {
  const [taskDetailDialogTaskId, setTaskDetailDialogTaskId] = useState<string | null>(null);
  const [showTaskDetailDialog, setShowTaskDetailDialog] = useState<boolean>(false);

  const onTableRowClick = (taskId: string) => {
    setTaskDetailDialogTaskId(taskId);
    setShowTaskDetailDialog(true);
  };

  return (
    <>
      <TaskDetailModal
        open={showTaskDetailDialog}
        taskId={taskDetailDialogTaskId}
        onClose={() => {
          setShowTaskDetailDialog(false);
        }}
        showBoardName
      />
      <Stack>
        <Title order={1}>Tasks</Title>
        <TaskListTable onRowClick={onTableRowClick} />
      </Stack>
    </>
  );
};
