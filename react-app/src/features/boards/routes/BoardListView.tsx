import { Button, Card, Group, Stack, Text, Title, useMantineTheme } from "@mantine/core";
import { useBoardListQuery } from "@/features/boards/apis/getBoardList.ts";
import { ReactNode, useState } from "react";
import { Link, useParams } from "react-router";
import { CreateBoardModal } from "@/features/boards/components/CreateBoardModal.tsx";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";

export const BoardListView = () => {
  const theme = useMantineTheme();
  const params = useParams();

  const userDetailsQuery = useCurrentUserDetailsQuery();

  const organizationId = params?.organizationId || null;

  const boardListQuery = useBoardListQuery(
    {
      organizationId: organizationId || "",
    },
    {
      enabled: organizationId != null,
    },
  );
  const [showCreateBoardDialog, setShowCreateBoardDialog] = useState<boolean>(false);

  const isPending = userDetailsQuery.isPending || boardListQuery.isPending;
  const isError = userDetailsQuery.isError || boardListQuery.isError;

  let listElement: ReactNode;
  if (isPending) {
    listElement = <Text>Loading...</Text>;
  } else if (isError) {
    listElement = <Text>Error loading boards</Text>;
  } else {
    const boardCards = boardListQuery.data.map((board) => (
      <Card
        key={board.id}
        component={Link}
        to={`/${board.organizationId}/boards/${board.id}`}
        style={{ cursor: "pointer", backgroundColor: theme.colors.stickyNote[0] }}
        shadow="paper"
      >
        <Text size="xl">{board.title}</Text>
      </Card>
    ));
    listElement = <Stack style={{ padding: "1rem", minHeight: "75vh" }}>{boardCards}</Stack>;
  }

  return (
    <>
      <CreateBoardModal
        open={showCreateBoardDialog}
        onClose={() => setShowCreateBoardDialog(false)}
        organizationId={organizationId || ""}
      />

      <Group justify="space-between" align="center" mb="1rem">
        <Title order={1}>Boards</Title>
        <Button variant="filled" onClick={() => setShowCreateBoardDialog(true)} color="primary">
          Create Board
        </Button>
      </Group>

      {listElement}
    </>
  );
};
