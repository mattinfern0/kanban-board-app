import { Controller, useForm } from "react-hook-form";
import { CreateTaskBody, CreateTaskFormValues } from "@/features/tasks/types";
import { useBoardQuery } from "@/features/boards/apis/getBoard.ts";
import { useCreateTaskMutation } from "@/features/tasks/apis/createTask.ts";
import { useEffect } from "react";
import { useSnackbar } from "notistack";
import { Button, Grid, Group, Modal, Select, Stack, Textarea, TextInput } from "@mantine/core";
import { TaskPrioritySelect } from "@/features/tasks/components/TaskPrioritySelect.tsx";

interface CreateTaskDialogProps {
  open: boolean;
  onClose: () => void;

  organizationId: string;
  boardId: string | null;
}

export const CreateTaskModal = (props: CreateTaskDialogProps) => {
  const { open, onClose, boardId } = props;
  const boardQuery = useBoardQuery(boardId);
  const createTaskMutation = useCreateTaskMutation();
  const { enqueueSnackbar } = useSnackbar();

  const boardTitle = boardQuery.data?.title || "";
  const boardColumns = boardQuery.data?.boardColumns || [];
  const defaultBoardColumnId: string = boardColumns.length > 0 ? boardColumns[0].id : "";
  const { control, handleSubmit, reset } = useForm<CreateTaskFormValues>({});

  useEffect(() => {
    reset({
      title: "",
      description: "",
      board_id: boardId || "",
      column_id: defaultBoardColumnId,
      priority: null,
    });
  }, [boardId, defaultBoardColumnId, reset]);

  const columnSelectOptions = boardColumns.map((column) => ({
    value: column.id,
    label: column.title,
  }));
  const isSubmitting = createTaskMutation.isPending;

  const handleClose = () => {
    reset();
    onClose();
  };

  const onSubmit = handleSubmit(
    (data) => {
      if (isSubmitting) {
        return;
      }

      const taskBody: CreateTaskBody = {
        organizationId: props.organizationId,
        title: data.title,
        description: data.description,
        boardColumnId: data.column_id,
        priority: data.priority || null,
      };

      createTaskMutation.mutate(taskBody, {
        onSuccess: () => {
          enqueueSnackbar("Task created!", { variant: "success" });
          handleClose();
        },
        onError: () => {
          enqueueSnackbar("An error occurred while creating this task.", { variant: "error" });
        },
      });
    },
    (errors) => {
      console.log(errors);
    },
  );

  return (
    <Modal opened={open} onClose={handleClose} size="xl" title="Create Task">
      <form onSubmit={onSubmit}>
        <Grid>
          <Grid.Col span={8}>
            <TextInput value={boardTitle} label="Board" width="100%" readOnly />
          </Grid.Col>
          <Grid.Col span={4}>
            <Controller
              control={control}
              name="column_id"
              render={({ field }) => <Select {...field} label="Column" data={columnSelectOptions} />}
            />
          </Grid.Col>
        </Grid>
        <Stack>
          <Controller
            control={control}
            name="title"
            render={({ field }) => <TextInput {...field} label="Title" required />}
          />

          <Controller
            control={control}
            name="description"
            render={({ field }) => <Textarea {...field} autosize minRows={5} label="Description" />}
          />

          <Controller
            control={control}
            name="priority"
            render={({ field }) => <TaskPrioritySelect {...field} label="Priority" />}
          />

          <Group justify="flex-end">
            <Button variant="filled" color="secondary" onClick={handleClose}>
              Cancel
            </Button>
            <Button variant="filled" type="submit" color="primary" disabled={isSubmitting}>
              Create
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  );
};
