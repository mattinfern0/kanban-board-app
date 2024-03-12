import { Dialog, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useTaskDetailQuery } from "@/features/boards/apis/getTaskDetail.ts";
import React from "react";

interface BoardTaskDetailProps {
  open: boolean;
  taskId: string;
  onClose: () => void;
}

export const BoardTaskDetail = (props: BoardTaskDetailProps) => {
  const { open, taskId, onClose } = props;
  const taskDetailQuery = useTaskDetailQuery(taskId);

  let dialogContent: React.ReactNode;

  if (taskDetailQuery.isPending) {
    dialogContent = <Typography>Loading...</Typography>;
  } else if (taskDetailQuery.isError) {
    dialogContent = <Typography>An error occurred</Typography>;
  } else {
    const task = taskDetailQuery.data;
    dialogContent = (
      <>
        <DialogTitle>
          {task.title} - {task.status}
        </DialogTitle>
        <DialogContent>
          <Typography>{task.description}</Typography>
        </DialogContent>
      </>
    );
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      {dialogContent}
    </Dialog>
  );
};
