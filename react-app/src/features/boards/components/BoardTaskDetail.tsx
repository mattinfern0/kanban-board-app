import {
  Dialog,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Menu,
  MenuItem,
  Stack,
  styled,
  TextField,
  Typography,
} from "@mui/material";
import { useTaskDetailQuery } from "@/features/boards/apis/getTaskDetail.ts";
import React from "react";
import { TaskStatusChip } from "@/components/misc/TaskStatusChip.tsx";
import { MoreVertRounded } from "@mui/icons-material";
import { PopoverProps } from "@mui/material/Popover";
import { useSnackbar } from "notistack";
import { useDeleteTaskMutation } from "@/features/tasks/apis/deleteTask.ts";

const HoverTextField = styled(TextField)({
  "& label.Mui-focused": {
    color: "#A0AAB4",
  },
  "& .MuiInput-underline:after": {
    borderBottomColor: "#B2BAC2",
  },
  "& .MuiOutlinedInput-root": {
    "& fieldset": {
      borderColor: "rgba(255,255,255,0)",
    },
    "&:hover fieldset": {
      borderColor: "#B2BAC2",
    },
    "&.Mui-focused fieldset": {
      borderColor: "#6F7E8C",
    },
  },
});

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
            <HoverTextField
              value={task.title}
              sx={{ width: "50vw" }}
              InputProps={{
                style: {
                  fontSize: "1.5rem",
                  fontWeight: "bold",
                },
              }}
            />
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
          <Grid container>
            <Grid item md={9}>
              <Typography component="label" htmlFor={"task-detail-description"}>
                Description
              </Typography>
              <HoverTextField
                id="task-detail-description"
                value={task.description}
                fullWidth
                multiline
                minRows={5}
                hiddenLabel
              />
            </Grid>
            <Grid item>
              <TaskStatusChip status={task.status} />
            </Grid>
          </Grid>
        </DialogContent>
      </>
    );
  }

  return (
    <Dialog open={open} onClose={handleDialogClose} maxWidth="xl" fullWidth>
      {dialogContent}
    </Dialog>
  );
};
