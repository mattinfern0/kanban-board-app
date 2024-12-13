import { Stack, Title } from "@mantine/core";
import { TaskListTable } from "@/features/tasks/components/TaskListTable.tsx";
import { useState } from "react";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";

export const TaskListView = () => {
  const [taskDetailDialogTaskId, setTaskDetailDialogTaskId] = useState<string | null>(null);
  const [showTaskDetailDialog, setShowTaskDetailDialog] = useState<boolean>(false);

  const onTableRowClick = (taskId: string) => {
    setTaskDetailDialogTaskId(taskId);
    setShowTaskDetailDialog(true);
  };

  return (
    <>
      <BoardTaskDetail
        open={showTaskDetailDialog}
        taskId={taskDetailDialogTaskId}
        organizationId={""} // TODO: Get organization ID from context
        onClose={() => {
          setShowTaskDetailDialog(false);
        }}
      />
      <Stack>
        <Title order={1}>Tasks</Title>
        <TaskListTable onRowClick={onTableRowClick} />
      </Stack>
    </>
  );
};
