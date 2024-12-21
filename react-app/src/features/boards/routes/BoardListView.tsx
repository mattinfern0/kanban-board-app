import { Button, Card, Group, Stack, Text, Title } from "@mantine/core";
import { useBoardListQuery } from "@/features/boards/apis/getBoardList.ts";
import { ReactNode, useState } from "react";
import { Link } from "react-router";
import { CreateBoardDialog } from "@/features/boards/components/CreateBoardDialog.tsx";

const MOCK_ORGANIZATION_ID = "846ba4b8-5556-4855-8fa6-b274dea3a3cc";

export const BoardListView = () => {
  const boardListQuery = useBoardListQuery();
  const [showCreateBoardDialog, setShowCreateBoardDialog] = useState<boolean>(false);

  let listElement: ReactNode;
  if (boardListQuery.isPending) {
    listElement = <Text>Loading...</Text>;
  } else if (boardListQuery.isError) {
    listElement = <Text>Error loading boards</Text>;
  } else {
    const boardCards = boardListQuery.data.map((board) => (
      <Card key={board.id} component={Link} to={`/boards/${board.id}`} style={{ cursor: "pointer" }} withBorder>
        <Text size="xl">{board.title}</Text>
      </Card>
    ));
    listElement = (
      <Stack style={{ backgroundColor: "lightgray", padding: "1rem", minHeight: "75vh" }}>{boardCards}</Stack>
    );
  }

  return (
    <>
      <CreateBoardDialog
        open={showCreateBoardDialog}
        onClose={() => setShowCreateBoardDialog(false)}
        organizationId={MOCK_ORGANIZATION_ID}
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
