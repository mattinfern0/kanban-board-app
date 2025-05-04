import { useTaskDetailQuery } from "@/features/boards/apis/getTaskDetail.ts";
import React, { useEffect } from "react";
import { TaskStatusChip } from "@/components/misc/TaskStatusChip.tsx";
import { useSnackbar } from "notistack";
import { useDeleteTaskMutation } from "@/features/tasks/apis/deleteTask.ts";
import { TaskPriority, UpdateTaskFormSchema, UpdateTaskFormValues } from "@/features/tasks/types";
import { Controller, useForm } from "react-hook-form";
import { useUpdateTaskMutation } from "@/features/tasks/apis/updateTask.ts";
import { useUpdateTaskAssigneesMutation } from "@/features/tasks/apis/updateTaskAssignees.ts";
import { ActionIcon, Badge, Grid, Group, Menu, Modal, Stack, Text, TextInput, Title } from "@mantine/core";
import { AssigneeSelect } from "@/features/tasks/components/AssigneeSelect.tsx";
import { TaskPrioritySelect } from "@/features/tasks/components/TaskPrioritySelect.tsx";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "react-router";
import { IconCancel, IconDeviceFloppy, IconDotsVertical } from "@tabler/icons-react";
import { TaskDescriptionEditor } from "@/features/tasks/components/TaskDescriptionEditor.tsx";
import { RichTextEditor } from "@mantine/tiptap";
import { Editor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";

interface BoardTaskDetailProps {
  open: boolean;
  taskId: string | null;
  onClose: () => void;
  showBoardName?: boolean;
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

interface DescriptionViewProps {
  value: string;
  onSave: (value: string) => void;
}

const DescriptionView = (props: Readonly<DescriptionViewProps>) => {
  const { value, onSave } = props;

  const [isEditing, setIsEditing] = React.useState<boolean>(false);
  const [internalValue, setInternalValue] = React.useState<string>(value);

  useEffect(() => {
    setInternalValue(value);
  }, [value]);

  if (!isEditing) {
    const readOnlyEditor = new Editor({
      extensions: [StarterKit],
      editable: false,
      content: internalValue,
    });
    return (
      <RichTextEditor
        editor={readOnlyEditor}
        onClick={() => setIsEditing(true)}
        styles={{ root: { cursor: "pointer" }, content: { minHeight: "10rem" } }}
      >
        <RichTextEditor.Content />
      </RichTextEditor>
    );
  }

  const onSaveClick = () => {
    onSave(internalValue);
    setIsEditing(false);
  };

  const onCancelClick = () => {
    setInternalValue(value);
    setIsEditing(false);
  };

  return (
    <Stack>
      <TaskDescriptionEditor value={internalValue} onChange={(v) => setInternalValue(v)} />
      <Group justify="end">
        <ActionIcon onClick={onCancelClick} color="secondary">
          <IconCancel />
        </ActionIcon>
        <ActionIcon onClick={onSaveClick} color="primary">
          <IconDeviceFloppy />
        </ActionIcon>
      </Group>
    </Stack>
  );
};

export const TaskDetailModal = (props: BoardTaskDetailProps) => {
  const { open, taskId, onClose, showBoardName } = props;
  const taskDetailQuery = useTaskDetailQuery(taskId);
  const organizationDetailsQuery = useOrganizationDetailQuery(taskDetailQuery.data?.organizationId || "", {
    enabled: !!taskDetailQuery.data?.organizationId,
  });
  const updateTaskMutation = useUpdateTaskMutation();
  const updateTaskAssigneesMutation = useUpdateTaskAssigneesMutation();
  const deleteTaskMutation = useDeleteTaskMutation();
  const { enqueueSnackbar } = useSnackbar();
  const { control, reset, handleSubmit, setValue, watch } = useForm<UpdateTaskFormValues>({
    resolver: zodResolver(UpdateTaskFormSchema),
  });

  useEffect(() => {
    const newValues: UpdateTaskFormValues = {
      title: taskDetailQuery.data?.title || "",
      description: taskDetailQuery.data?.description || "",
      priority: taskDetailQuery.data?.priority || null,
      assignees: taskDetailQuery.data?.assignees.map((assignee) => assignee.userId) || [],
    };

    reset(newValues);
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
            organizationId: taskDetailQuery.data.organizationId,
            boardColumnId: oldTaskData.boardColumnId,
            boardColumnOrder: oldTaskData.boardColumnOrder,
            status: oldTaskData.status,
            priority: data.priority,
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
    console.debug("Assignee blur");
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

  const assigneeOptions =
    organizationDetailsQuery.data?.members.map((member) => ({
      id: member.userId,
      firstName: member.firstName,
      lastName: member.lastName,
    })) || [];

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
    const onDescriptionSave = (value: string) => {
      if (value === taskDetailQuery.data.description) {
        return;
      }

      setValue("description", value, { shouldDirty: true, shouldTouch: true });
      onSubmit();
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
                <IconDotsVertical />
              </ActionIcon>
            </Menu.Target>
            <DetailMenu onDeleteClick={handleTaskDelete} />
          </Menu>
        </Group>

        <Grid gutter="xl">
          <Grid.Col span={9}>
            <Stack>
              <Text fw="bold">Description</Text>
              <DescriptionView value={taskDetailQuery.data.description} onSave={onDescriptionSave} />
            </Stack>
          </Grid.Col>
          <Grid.Col span={3}>
            <Stack>
              {showBoardName && task.board != null && (
                <Badge
                  component={Link}
                  color="primary"
                  size="xl"
                  radius="md"
                  variant="filled"
                  to={`/${taskDetailQuery.data?.organizationId}/boards/${task.board.id}`}
                  styles={{ root: { cursor: "pointer" } }}
                >
                  {task.board.title}
                </Badge>
              )}

              <TaskStatusChip status={task.status} />

              <Stack>
                <Text component="label" fw="bold">
                  Priority
                </Text>
                <Controller
                  control={control}
                  name="priority"
                  render={({ field }) => (
                    <TaskPrioritySelect
                      onBlur={async (e) => {
                        if (field.value !== task.priority) {
                          await onSubmit(e);
                        }
                      }}
                      onChange={(value) => {
                        setValue("priority", value as TaskPriority | null, { shouldDirty: true, shouldTouch: true });
                      }}
                      value={field.value}
                    />
                  )}
                />
              </Stack>

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
