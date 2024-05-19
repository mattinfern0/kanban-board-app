import { Button, Card, CardActionArea, CardContent, Stack, Typography } from "@mui/material";
import { useBoardListQuery } from "@/features/boards/apis/getBoardList.ts";
import { ReactNode } from "react";
import { Link } from "react-router-dom";

export const BoardListView = () => {
  const boardListQuery = useBoardListQuery();

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
      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Boards</Typography>
        <Button variant="contained">Create Board</Button>
      </Stack>

      {listElement}
    </>
  );
};
