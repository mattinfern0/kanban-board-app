import { Dialog, DialogContent, DialogTitle, IconButton, Menu, MenuItem, Stack, Typography } from "@mui/material";
import { useTaskDetailQuery } from "@/features/boards/apis/getTaskDetail.ts";
import React from "react";
import { TaskStatusChip } from "@/components/misc/TaskStatusChip.tsx";
import { MoreVertRounded } from "@mui/icons-material";
import { PopoverProps } from "@mui/material/Popover";
import { useSnackbar } from "notistack";
import { useDeleteTaskMutation } from "@/features/tasks/apis/deleteTask.ts";

interface BoardTaskDetailProps {
  open: boolean;
  taskId: string | null;
  onClose: () => void;
}

interface DetailMenuProps {
  anchorEl: PopoverProps["anchorEl"];

  open: boolean;
  onClose: () => void;

  onDeleteClick: () => void;
}

const DetailMenu = (props: DetailMenuProps) => {
  const { open, onClose, onDeleteClick, anchorEl } = props;
  return (
    <Menu open={open} onClose={onClose} anchorEl={anchorEl}>
      <MenuItem onClick={onDeleteClick}>Delete</MenuItem>
    </Menu>
  );
};

export const BoardTaskDetail = (props: BoardTaskDetailProps) => {
  const { open, taskId, onClose } = props;
  const taskDetailQuery = useTaskDetailQuery(taskId);
  const deleteTaskMutation = useDeleteTaskMutation();
  const { enqueueSnackbar } = useSnackbar();

  const [menuAnchorEl, setMenuAnchorEl] = React.useState<null | HTMLElement>(null);
  const menuOpen = Boolean(menuAnchorEl);

  const handleMenuButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setMenuAnchorEl(event.currentTarget);
  };
  const handleMenuClose = () => {
    setMenuAnchorEl(null);
  };

  const handleDialogClose = () => {
    handleMenuClose();
    onClose();
  };

  const handleTaskDelete = () => {
    if (!taskId || deleteTaskMutation.isPending) return;

    deleteTaskMutation.mutate(taskId, {
      onSuccess: () => {
        enqueueSnackbar("Task deleted.", { variant: "success" });
        handleDialogClose();
      },
      onError: () => {
        enqueueSnackbar("An error occurred while deleting the task.", { variant: "error" });
      },
    });
  };

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
          <Stack direction="row" justifyContent="space-between">
            <span>
              {task.title} <TaskStatusChip status={task.status} />
            </span>
            <IconButton onClick={handleMenuButtonClick}>
              <MoreVertRounded />
            </IconButton>
            <DetailMenu
              open={menuOpen}
              anchorEl={menuAnchorEl}
              onClose={handleMenuClose}
              onDeleteClick={handleTaskDelete}
            />
          </Stack>
        </DialogTitle>
        <DialogContent>
          <Typography>{task.description}</Typography>
        </DialogContent>
      </>
    );
  }

  return (
    <Dialog open={open} onClose={handleDialogClose} maxWidth="md" fullWidth>
      {dialogContent}
    </Dialog>
  );
};
