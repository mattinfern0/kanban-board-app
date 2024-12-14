import { useTaskDetailQuery } from "@/features/boards/apis/getTaskDetail.ts";
import React, { useEffect } from "react";
import { TaskStatusChip } from "@/components/misc/TaskStatusChip.tsx";
import { MoreVertRounded } from "@mui/icons-material";
import { useSnackbar } from "notistack";
import { useDeleteTaskMutation } from "@/features/tasks/apis/deleteTask.ts";
import { UpdateTaskFormValues } from "@/features/tasks/types";
import { Controller, useForm } from "react-hook-form";
import { useUpdateTaskMutation } from "@/features/tasks/apis/updateTask.ts";
import { useGetUsersQuery } from "@/features/users/apis/getUsers.ts";
import { useUpdateTaskAssigneesMutation } from "@/features/tasks/apis/updateTaskAssignees.ts";
import { ActionIcon, Grid, Group, Menu, Modal, Stack, Text, Textarea, TextInput, Title } from "@mantine/core";
import { AssigneeSelect } from "@/features/tasks/components/AssigneeSelect.tsx";

interface BoardTaskDetailProps {
  open: boolean;
  taskId: string | null;
  organizationId: string;
  onClose: () => void;
}

interface DetailMenuProps {
  onDeleteClick: () => void;
}

const DetailMenu = (props: DetailMenuProps) => {
  const { onDeleteClick } = props;
  return (
    <Menu.Dropdown>
      <Menu.Item onClick={onDeleteClick}>Delete</Menu.Item>
    </Menu.Dropdown>
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

  const handleDialogClose = () => {
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

  const assigneeOptions = organizationUsersQuery.data || [];

  if (taskDetailQuery.isPending) {
    dialogContent = <Title>Loading...</Title>;
  } else if (taskDetailQuery.isError) {
    dialogContent = <Title>An error occurred</Title>;
  } else {
    const task = taskDetailQuery.data;
    const onTitleInputKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
      // Prevent newlines in the title
      if (event.key === "Enter") {
        event.preventDefault();
        event.stopPropagation();
      }
    };
    dialogContent = (
      <form onSubmit={onSubmit}>
        <Group justify="space-between" align="center">
          <Controller
            control={control}
            name="title"
            render={({ field }) => (
              <TextInput
                {...field}
                style={{ width: "90%", fontWeight: "bold" }}
                size="xl"
                onKeyDown={onTitleInputKeyDown}
                onBlur={async (e) => {
                  if (field.value !== task.title) {
                    await onSubmit(e);
                  }
                }}
                variant="unstyled"
              />
            )}
          />

          <Menu>
            <Menu.Target>
              <ActionIcon variant="transparent" color="gray">
                <MoreVertRounded />
              </ActionIcon>
            </Menu.Target>
            <DetailMenu onDeleteClick={handleTaskDelete} />
          </Menu>
        </Group>

        <Grid>
          <Grid.Col span={9}>
            <Stack>
              <Text component="label" htmlFor="task-detail-description" fw="bold">
                Description
              </Text>
              <Controller
                control={control}
                name="description"
                render={({ field }) => (
                  <Textarea
                    {...field}
                    id="task-detail-description"
                    autosize
                    minRows={5}
                    onBlur={async (e) => {
                      if (field.value !== task.description) {
                        await onSubmit(e);
                      }
                    }}
                    variant="unstyled"
                  />
                )}
              />
            </Stack>
          </Grid.Col>
          <Grid.Col span={3}>
            <Stack>
              <TaskStatusChip status={task.status} />

              <Stack>
                <Text component="label" htmlFor="assignee-select" fw="bold">
                  Assignees
                </Text>
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
                      assigneeOptions={assigneeOptions}
                    />
                  )}
                />
              </Stack>
            </Stack>
          </Grid.Col>
        </Grid>
      </form>
    );
  }

  return (
    <Modal
      opened={open}
      onClose={handleDialogClose}
      size="75%"
      withCloseButton={false}
      transitionProps={{ transition: "fade" }}
      styles={{
        body: {
          padding: "1.5rem",
        },
      }}
    >
      {dialogContent}
    </Modal>
  );
};
