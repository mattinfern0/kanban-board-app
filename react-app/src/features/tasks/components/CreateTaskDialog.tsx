import { Controller, useForm } from "react-hook-form";
import { CreateTaskFormValues } from "@/features/tasks/types";
import {
  Button,
  Dialog,
  DialogContent,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
} from "@mui/material";
import { useBoardQuery } from "@/features/boards/apis/getBoard.ts";

interface CreateTaskDialogProps {
  open: boolean;
  onClose: () => void;

  organizationId: string;
  boardId: string | null;
}

export const CreateTaskDialog = (props: CreateTaskDialogProps) => {
  const { open, onClose, boardId } = props;
  const boardQuery = useBoardQuery(boardId);

  const boardTitle = boardQuery.data?.title || "";
  const boardColumns = boardQuery.data?.boardColumns || [];
  const { control, handleSubmit, reset } = useForm<CreateTaskFormValues>({
    defaultValues: {
      title: "",
      description: "",
      column_id: boardColumns.length > 0 ? boardColumns[0].id : "",
    },
  });

  const columnSelectOptions = boardColumns.map((column) => (
    <MenuItem key={column.id} value={column.id}>
      {column.title}
    </MenuItem>
  ));

  const onSubmit = handleSubmit(
    (data) => {
      console.log(data);
    },
    (errors) => {
      console.log(errors);
    },
  );

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Dialog open={open} maxWidth="lg" fullWidth onClose={handleClose}>
      <DialogContent>
        <form onSubmit={onSubmit}>
          <Grid container spacing={3} mb={3}>
            <Grid item md={8}>
              <TextField value={boardTitle} InputProps={{ readOnly: true }} label="Board" fullWidth />
            </Grid>
            <Grid item md={4}>
              <FormControl fullWidth>
                <InputLabel id="column-select-label">Column</InputLabel>
                <Controller
                  control={control}
                  name="column_id"
                  render={({ field }) => (
                    <Select {...field} labelId="column-select-label" label="Column">
                      {columnSelectOptions}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>
          </Grid>
          <Stack spacing={3}>
            <Controller control={control} name="title" render={({ field }) => <TextField {...field} label="Title" />} />

            <Controller
              control={control}
              name="description"
              render={({ field }) => <TextField {...field} multiline minRows={5} label="Description" />}
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
