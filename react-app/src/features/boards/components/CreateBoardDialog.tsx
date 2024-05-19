import { Controller, useForm } from "react-hook-form";
import { Button, Dialog, DialogContent, DialogTitle, Stack, TextField } from "@mui/material";
import { CreateBoardFormValues } from "@/features/boards/types";
import { useSnackbar } from "notistack";
import { useCreateBoardMutation } from "@/features/boards/apis/createBoard.ts";
import { useNavigate } from "react-router-dom";

interface CreateTaskDialogProps {
  open: boolean;
  onClose: () => void;

  organizationId: string;
}

export const CreateBoardDialog = (props: CreateTaskDialogProps) => {
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
    <Dialog open={open} maxWidth="md" fullWidth onClose={handleClose}>
      <DialogTitle>Create Board</DialogTitle>
      <DialogContent>
        <form onSubmit={onSubmit}>
          <Stack spacing={3}>
            <Controller
              control={control}
              name="title"
              render={({ field }) => <TextField {...field} label="Title" required />}
            />

            <Stack direction="row-reverse" spacing={3}>
              <Button variant="contained" type="submit">
                Create
              </Button>
              <Button variant="contained" color="secondary" onClick={handleClose}>
                Cancel
              </Button>
            </Stack>
          </Stack>
        </form>
      </DialogContent>
    </Dialog>
  );
};
