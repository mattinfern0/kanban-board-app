import { Link, useParams } from "react-router";
import { useBoardQuery } from "@/features/boards/apis/getBoard.ts";
import { useState } from "react";
import { DeleteBoardModal } from "@/features/boards/components/DeleteBoardModal.tsx";
import { Controller, useForm } from "react-hook-form";
import { BoardDetail } from "@/features/boards/types";
import { useUpdateBoardHeaderMutation } from "@/features/boards/apis/updateBoardHeader.ts";
import { useSnackbar } from "notistack";
import { Button, Card, Group, Stack, Tabs, Text, TextInput, Title } from "@mantine/core";
import { IconChevronLeft } from "@tabler/icons-react";

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
      <Stack>
        <Controller
          control={control}
          name="title"
          render={({ field }) => <TextInput {...field} label="Title" required />}
        />

        <Button type="submit" variant="filled" color="primary">
          Save Changes
        </Button>
      </Stack>
    </form>
  );
};

export const BoardSettings = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [showDeleteBoardDialog, setShowDeleteBoardDialog] = useState<boolean>(false);

  let element;

  if (boardQuery.isLoading) {
    element = <Text>Loading...</Text>;
  } else if (boardQuery.isError) {
    element = <Text>Error loading board</Text>;
  } else if (boardId != null && boardQuery.isSuccess) {
    element = (
      <>
        <Card>
          <Tabs defaultValue="settings">
            <Tabs.List mb="1.5rem">
              <Tabs.Tab value="settings">Info</Tabs.Tab>
              <Tabs.Tab value="danger">Danger</Tabs.Tab>
            </Tabs.List>

            <Tabs.Panel value="settings">
              <UpdateBoardHeaderForm board={boardQuery.data} />
            </Tabs.Panel>

            <Tabs.Panel value="danger">
              <Button variant="outline" color="danger" onClick={() => setShowDeleteBoardDialog(true)}>
                Delete Board
              </Button>
            </Tabs.Panel>
          </Tabs>
        </Card>
        <DeleteBoardModal
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
      <Group mb="1rem" align="center">
        <Button
          component={Link}
          to={`/boards/${boardId}`}
          leftSection={<IconChevronLeft />}
          variant="filled"
          color="secondary"
        >
          Back
        </Button>
        <Title order={3}>Board Settings</Title>
      </Group>
      {element}
    </>
  );
};
