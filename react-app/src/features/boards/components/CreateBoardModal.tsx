import { Controller, useForm } from "react-hook-form";
import { CreateBoardFormValues } from "@/features/boards/types";
import { useSnackbar } from "notistack";
import { useCreateBoardMutation } from "@/features/boards/apis/createBoard.ts";
import { useNavigate } from "react-router";
import { Button, Group, Modal, Stack, TextInput } from "@mantine/core";

interface CreateTaskDialogProps {
  open: boolean;
  onClose: () => void;

  organizationId: string;
}

export const CreateBoardModal = (props: CreateTaskDialogProps) => {
  const { open, onClose } = props;
  const { enqueueSnackbar } = useSnackbar();
  const createBoardMutation = useCreateBoardMutation();
  const navigate = useNavigate();

  const { control, handleSubmit, reset } = useForm<CreateBoardFormValues>({
    defaultValues: {
      title: "",
    },
  });

  const handleClose = () => {
    reset();
    onClose();
  };

  const onSubmit = handleSubmit(
    (data) => {
      console.debug(data);
      createBoardMutation.mutate(
        { organizationId: props.organizationId, title: data.title },
        {
          onSuccess: (data) => {
            reset();
            enqueueSnackbar("Board created! Start adding some tasks!", { variant: "success" });
            navigate(`/boards/${data.id}`);
          },
          onError: (error) => {
            enqueueSnackbar("Error creating board", { variant: "error" });
            console.error(error);
          },
        },
      );
    },
    (errors) => {
      console.log(errors);
    },
  );

  return (
    <Modal
      opened={open}
      onClose={handleClose}
      size="xl"
      title="Create Board"
      styles={{ title: { fontSize: "1.5em" }, close: { display: "none" } }}
    >
      <form onSubmit={onSubmit}>
        <Stack>
          <Controller
            control={control}
            name="title"
            render={({ field }) => <TextInput {...field} label="Title" required size="md" />}
          />

          <Group justify="flex-end">
            <Button color="secondary" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" color="primary">
              Create
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  );
};
