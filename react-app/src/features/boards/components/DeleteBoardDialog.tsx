import {
  Button,
  Checkbox,
  Dialog,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  InputLabel,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { useSnackbar } from "notistack";
import { useNavigate } from "react-router-dom";
import { useDeleteBoardMutation } from "@/features/boards/apis/deleteBoard.ts";

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
    <Dialog open={open} maxWidth="md" fullWidth onClose={onClose}>
      <DialogTitle>Delete Board</DialogTitle>
      <DialogContent>
        <Stack spacing={4}>
          <Typography variant="body1">Are you sure you want to delete this board?</Typography>
          <form onSubmit={onSubmit} id={DELETE_FORM_ID}>
            <Stack spacing={1}>
              <InputLabel htmlFor="confirmTitle">Enter the board's title {boardTitle} to confirm</InputLabel>
              <Controller
                control={control}
                name="confirmTitle"
                render={({ field }) => <TextField {...field} fullWidth size="small" required />}
              />

              <Controller
                control={control}
                name="deleteTasks"
                render={({ field }) => (
                  <FormControlLabel
                    control={<Checkbox {...field} checked={field.value} color="error" />}
                    label="Delete All Tasks in This Board"
                  />
                )}
              />
            </Stack>
          </form>
          <Stack direction="row" justifyContent="space-between">
            <Button variant="contained" onClick={onClose} color="secondary">
              Cancel
            </Button>
            <Button
              variant="contained"
              color="error"
              type="submit"
              form={DELETE_FORM_ID}
              disabled={deleteButtonDisabled}
            >
              Delete
            </Button>
          </Stack>
        </Stack>
      </DialogContent>
    </Dialog>
  );
};
