import { useBoardQuery } from "../apis/getBoard.ts";
import { Typography } from "@mui/material";
import { useParams } from "react-router-dom";

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
  return (
    <>
      <Typography variant="h3">{boardQuery.data.title}</Typography>
    </>
  );
};
