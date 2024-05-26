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
import React, { useEffect } from "react";
import { TaskStatusChip } from "@/components/misc/TaskStatusChip.tsx";
import { MoreVertRounded } from "@mui/icons-material";
import { PopoverProps } from "@mui/material/Popover";
import { useSnackbar } from "notistack";
import { useDeleteTaskMutation } from "@/features/tasks/apis/deleteTask.ts";
import { UpdateTaskFormValues } from "@/features/tasks/types";
import { Controller, useForm } from "react-hook-form";
import { useUpdateTaskMutation } from "@/features/tasks/apis/updateTask.ts";
import { AssigneeSelect, AssigneeSelectOption } from "@/features/tasks/components/AssigneeSelect.tsx";
import { useGetUsersQuery } from "@/features/users/apis/getUsers.ts";
import { useUpdateTaskAssigneesMutation } from "@/features/tasks/apis/updateTaskAssignees.ts";

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
  organizationId: string;
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
  const organizationUsersQuery = useGetUsersQuery({ organizationId: props.organizationId });
  const updateTaskMutation = useUpdateTaskMutation();
  const updateTaskAssigneesMutation = useUpdateTaskAssigneesMutation();
  const deleteTaskMutation = useDeleteTaskMutation();
  const { enqueueSnackbar } = useSnackbar();
  const { control, reset, handleSubmit, setValue, watch } = useForm<UpdateTaskFormValues>();

  useEffect(() => {
    reset({
      title: taskDetailQuery.data?.title || "",
      description: taskDetailQuery.data?.description || "",
      assignees: taskDetailQuery.data?.assignees.map((assignee) => assignee.userId) || [],
    });
  }, [reset, taskDetailQuery.data]);

  const [menuAnchorEl, setMenuAnchorEl] = React.useState<null | HTMLElement>(null);
  const menuOpen = Boolean(menuAnchorEl);

  // TODO figure how to only call this if the form data is different.
  const onSubmit = handleSubmit(
    (data) => {
      if (!taskDetailQuery.data) {
        return;
      }

      console.debug("Updating task");
      console.debug(data);

      const oldTaskData = taskDetailQuery.data;

      const newTaskData = structuredClone(taskDetailQuery.data);
      newTaskData.title = data.title;
      newTaskData.description = data.description;

      updateTaskMutation.mutate(
        {
          taskId: oldTaskData.id,
          body: {
            title: data.title,
            description: data.description,
            organizationId: props.organizationId,
            boardColumnId: oldTaskData.boardColumnId,
            boardColumnOrder: oldTaskData.boardColumnOrder,
            status: oldTaskData.status,
          },
        },
        {
          onError: () => {
            enqueueSnackbar("An error occurred while updating the task.", { variant: "error" });
          },
        },
      );
    },
    (errors) => {
      console.debug(errors);
    },
  );

  const onAssigneeBlur = () => {
    if (taskId == null || updateTaskAssigneesMutation.isPending) {
      return;
    }

    updateTaskAssigneesMutation.mutate(
      {
        taskId: taskId,
        assigneeIds: watch("assignees"),
      },
      {
        onError: () => {
          enqueueSnackbar("An error occurred while updating the assignees.", { variant: "error" });
        },
      },
    );
  };

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

  const assigneeOptions: AssigneeSelectOption[] =
    organizationUsersQuery.data?.map((user) => ({
      value: user.id,
      label: `${user.firstName} ${user.lastName}`,
    })) || [];

  if (taskDetailQuery.isPending) {
    dialogContent = <Typography>Loading...</Typography>;
  } else if (taskDetailQuery.isError) {
    dialogContent = <Typography>An error occurred</Typography>;
  } else {
    const task = taskDetailQuery.data;
    dialogContent = (
      <form onSubmit={onSubmit}>
        <DialogTitle>
          <Stack direction="row" justifyContent="space-between">
            <Controller
              control={control}
              name="title"
              render={({ field }) => (
                <HoverTextField
                  {...field}
                  sx={{ width: "50vw" }}
                  InputProps={{
                    style: {
                      fontSize: "1.5rem",
                      fontWeight: "bold",
                    },
                  }}
                  onBlur={onSubmit}
                />
              )}
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
          <Grid container spacing={3}>
            <Grid item md={9}>
              <Stack>
                <Typography component="label" htmlFor={"task-detail-description"} fontWeight="bold">
                  Description
                </Typography>
                <Controller
                  control={control}
                  name="description"
                  render={({ field }) => (
                    <HoverTextField
                      {...field}
                      id="task-detail-description"
                      sx={{ width: "75%" }}
                      multiline
                      minRows={5}
                      hiddenLabel
                      onBlur={onSubmit}
                    />
                  )}
                />
              </Stack>
            </Grid>
            <Grid item md={3}>
              <Stack spacing={3}>
                <TaskStatusChip status={task.status} />

                <Stack>
                  <Typography component="label" htmlFor={"assignee-select"} fontWeight="bold">
                    Assignees
                  </Typography>
                  <Controller
                    control={control}
                    name="assignees"
                    render={({ field }) => (
                      <AssigneeSelect
                        inputLabel="assignee-select"
                        onBlur={onAssigneeBlur}
                        onChange={(value) => {
                          setValue("assignees", value, { shouldDirty: true, shouldTouch: true });
                        }}
                        value={field.value}
                        options={assigneeOptions}
                      />
                    )}
                  />
                </Stack>
              </Stack>
            </Grid>
          </Grid>
        </DialogContent>
      </form>
    );
  }

  return (
    <Dialog open={open} onClose={handleDialogClose} maxWidth="lg" fullWidth>
      {dialogContent}
    </Dialog>
  );
};
