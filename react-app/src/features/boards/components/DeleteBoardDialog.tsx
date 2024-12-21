import { Controller, useForm } from "react-hook-form";
import { useSnackbar } from "notistack";
import { useNavigate } from "react-router";
import { useDeleteBoardMutation } from "@/features/boards/apis/deleteBoard.ts";
import { Button, Checkbox, Group, Modal, Stack, Text, TextInput } from "@mantine/core";

interface DeleteBoardDialogProps {
  open: boolean;
  onClose: () => void;

  boardId: string;
  boardTitle: string;
}

const DELETE_FORM_ID = "delete-form";

type DeleteFormValues = {
  confirmTitle: string;
  deleteTasks: boolean;
};

export const DeleteBoardDialog = (props: DeleteBoardDialogProps) => {
  const { open, onClose, boardId, boardTitle } = props;
  const deleteBoardMutation = useDeleteBoardMutation();
  const { control, handleSubmit, watch } = useForm<DeleteFormValues>({
    defaultValues: {
      confirmTitle: "",
      deleteTasks: false,
    },
  });
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  const onSubmit = handleSubmit(
    (data) => {
      if (deleteBoardMutation.isPending) {
        return;
      }

      deleteBoardMutation.mutate(
        { boardId, deleteTasks: data.deleteTasks },
        {
          onSuccess: () => {
            enqueueSnackbar("Board deleted!", { variant: "success" });
            navigate("/boards");
          },
          onError: (error) => {
            console.error(error);
            enqueueSnackbar("Failed to delete board", { variant: "error" });
          },
        },
      );
    },
    (errors) => {
      console.debug(errors);
    },
  );

  const confirmTitleValue = watch("confirmTitle");
  const deleteButtonDisabled = confirmTitleValue !== boardTitle;

  return (
    <Modal
      opened={open}
      size="xl"
      onClose={onClose}
      title="Delete Board"
      styles={{ title: { fontSize: "1.5em" }, close: { display: "none" } }}
    >
      <Stack>
        <Text>Are you sure you want to delete this board?</Text>
        <form onSubmit={onSubmit} id={DELETE_FORM_ID}>
          <Stack>
            <Text component="label" htmlFor="confirmTitleInput">
              Enter the board's title ({boardTitle}) to confirm
            </Text>
            <Controller
              control={control}
              name="confirmTitle"
              render={({ field }) => <TextInput {...field} id="confirmTitleInput" w="100%" required />}
            />

            <Controller
              control={control}
              name="deleteTasks"
              render={({ field }) => (
                <Checkbox
                  onChange={field.onChange}
                  checked={field.value}
                  label="Delete All Tasks in This Board"
                  color="red"
                />
              )}
            />
          </Stack>
        </form>
        <Group justify="space-between">
          <Button variant="filled" onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button variant="filled" color="danger" type="submit" form={DELETE_FORM_ID} disabled={deleteButtonDisabled}>
            Delete
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
};
