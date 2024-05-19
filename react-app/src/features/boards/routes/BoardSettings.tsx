import { Box, Button, Card, CardContent, Stack, Tab, TextField, Typography } from "@mui/material";
import { Link, useParams } from "react-router-dom";
import { useBoardQuery } from "@/features/boards/apis/getBoard.ts";
import React, { useState } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { ChevronLeft } from "@mui/icons-material";
import { DeleteBoardDialog } from "@/features/boards/components/DeleteBoardDialog.tsx";
import { Controller, useForm } from "react-hook-form";
import { BoardDetail } from "@/features/boards/types";
import { useUpdateBoardHeaderMutation } from "@/features/boards/apis/updateBoardHeader.ts";
import { useSnackbar } from "notistack";

type TabValue = "settings" | "danger";

type UpdateBoardHeaderFormValues = {
  title: string;
};

interface UpdateBoardHeaderFormProps {
  board: BoardDetail;
}

const UpdateBoardHeaderForm = (props: UpdateBoardHeaderFormProps) => {
  const { control, handleSubmit } = useForm<UpdateBoardHeaderFormValues>({
    defaultValues: {
      title: props.board.title,
    },
  });
  const updateBoardHeaderMutation = useUpdateBoardHeaderMutation();
  const { enqueueSnackbar } = useSnackbar();

  const onSubmit = handleSubmit(
    (data) => {
      if (updateBoardHeaderMutation.isPending) {
        return;
      }
      updateBoardHeaderMutation.mutate(
        {
          boardId: props.board.id,
          body: {
            title: data.title,
          },
        },
        {
          onSuccess: () => {
            enqueueSnackbar("Saved changes!", { variant: "success" });
          },
          onError: () => {
            enqueueSnackbar("An error occurred while saving chages.", { variant: "error" });
          },
        },
      );
    },
    (formErrors) => {
      console.debug(formErrors);
    },
  );

  return (
    <form onSubmit={onSubmit}>
      <Stack spacing={3}>
        <Controller
          control={control}
          name="title"
          render={({ field }) => <TextField {...field} label="Title" variant="outlined" size="small" required />}
        />

        <Button type="submit" variant="contained" size="small">
          Save Changes
        </Button>
      </Stack>
    </form>
  );
};

export const BoardSettings = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [activeTab, setActiveTab] = useState<TabValue>("settings");
  const [showDeleteBoardDialog, setShowDeleteBoardDialog] = useState<boolean>(false);

  let element;

  if (boardQuery.isLoading) {
    element = <Typography>Loading...</Typography>;
  } else if (boardQuery.isError) {
    element = <Typography>Error loading board</Typography>;
  } else if (boardId != null && boardQuery.isSuccess) {
    const handleTabChange = (_event: React.SyntheticEvent, newValue: TabValue) => {
      setActiveTab(newValue);
    };

    element = (
      <>
        <Card>
          <CardContent>
            <TabContext value={activeTab}>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <TabList onChange={handleTabChange}>
                  <Tab label="Info" value="settings" />
                  <Tab label="Danger" value="danger" />
                </TabList>
              </Box>
              <TabPanel value="settings">
                <Stack spacing={3}>
                  <Typography>Info</Typography>
                  <UpdateBoardHeaderForm board={boardQuery.data} />
                </Stack>
              </TabPanel>

              <TabPanel value="danger">
                <Button variant="outlined" color="error" onClick={() => setShowDeleteBoardDialog(true)}>
                  Delete Board
                </Button>
              </TabPanel>
            </TabContext>
          </CardContent>
        </Card>
        <DeleteBoardDialog
          open={showDeleteBoardDialog}
          onClose={() => setShowDeleteBoardDialog(false)}
          boardId={boardId}
          boardTitle={boardQuery.data.title}
        />
      </>
    );
  }

  return (
    <>
      <Stack direction="row" mb={3} alignItems="center" spacing={3}>
        <Button
          component={Link}
          to={`/boards/${boardId}`}
          startIcon={<ChevronLeft />}
          variant="contained"
          sx={{ mb: 3 }}
        >
          Back
        </Button>
        <Typography variant="h4">Board Settings</Typography>
      </Stack>
      {element}
    </>
  );
};
