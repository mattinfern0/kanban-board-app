import { Button, Card, CardActionArea, CardContent, Stack, Typography } from "@mui/material";
import { useBoardListQuery } from "@/features/boards/apis/getBoardList.ts";
import { ReactNode, useState } from "react";
import { Link } from "react-router-dom";
import { CreateBoardDialog } from "@/features/boards/components/CreateBoardDialog.tsx";

const MOCK_ORGANIZATION_ID = "846ba4b8-5556-4855-8fa6-b274dea3a3cc";

export const BoardListView = () => {
  const boardListQuery = useBoardListQuery();
  const [showCreateBoardDialog, setShowCreateBoardDialog] = useState<boolean>(false);

  let listElement: ReactNode;
  if (boardListQuery.isPending) {
    listElement = <Typography>Loading...</Typography>;
  } else if (boardListQuery.isError) {
    listElement = <Typography>Error loading boards</Typography>;
  } else {
    const boardCards = boardListQuery.data.map((board) => (
      <Card key={board.id} sx={{ cursor: "pointer" }}>
        <CardActionArea component={Link} to={`/boards/${board.id}`}>
          <CardContent>
            <Typography variant="h5">{board.title}</Typography>
          </CardContent>
        </CardActionArea>
      </Card>
    ));
    listElement = (
      <Stack spacing={5} bgcolor="lightgray" padding={3} borderRadius={3} minHeight="75vh">
        {boardCards}
      </Stack>
    );
  }

  return (
    <>
      <CreateBoardDialog
        open={showCreateBoardDialog}
        onClose={() => setShowCreateBoardDialog(false)}
        organizationId={MOCK_ORGANIZATION_ID}
      />

      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Boards</Typography>
        <Button variant="contained" onClick={() => setShowCreateBoardDialog(true)}>
          Create Board
        </Button>
      </Stack>

      {listElement}
    </>
  );
};
