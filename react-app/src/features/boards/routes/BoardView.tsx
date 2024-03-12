import { useBoardQuery } from "../apis/getBoard.ts";
import { Grid, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";

export const BoardView = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");

  if (boardQuery.isPending) {
    return <Typography>Loading...</Typography>;
  }

  if (boardQuery.isError) {
    console.error(boardQuery.error);
    return <Typography>Error!</Typography>;
  }

  console.debug(boardQuery.data);

  const board = boardQuery.data;

  const gridColumnSize = 12 / board.boardColumns.length;
  const columnElements = board.boardColumns.map((c) => (
    <Grid item md={gridColumnSize}>
      <BoardColumn key={c.id} boardColumn={c} />
    </Grid>
  ));
  return (
    <>
      <Typography variant="h3">{boardQuery.data.title}</Typography>
      <Grid container spacing={3}>
        {columnElements}
      </Grid>
    </>
  );
};
