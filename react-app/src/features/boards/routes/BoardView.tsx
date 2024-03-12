import { useBoardQuery } from "../apis/getBoard.ts";
import { Grid, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { BoardColumn } from "@/features/boards/components/BoardColumn.tsx";
import { useState } from "react";
import { BoardTask } from "@/features/boards/types";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";

export const BoardView = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [showTaskDialog, setShowTaskDialog] = useState<boolean>(false);
  const [taskDialogTaskId, setTaskDialogTaskId] = useState<string | null>(null);

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
  const onTaskCardClick = (task: BoardTask) => {
    setTaskDialogTaskId(task.id);
    setShowTaskDialog(true);
  };
  const columnElements = board.boardColumns.map((c) => (
    <Grid key={c.id} item md={gridColumnSize}>
      <BoardColumn boardColumn={c} onTaskCardClick={onTaskCardClick} />
    </Grid>
  ));
  return (
    <>
      <BoardTaskDetail
        open={showTaskDialog}
        taskId={taskDialogTaskId || ""}
        onClose={() => {
          setShowTaskDialog(false);
        }}
      />
      <Typography variant="h3">{boardQuery.data.title}</Typography>
      <Grid container spacing={3}>
        {columnElements}
      </Grid>
    </>
  );
};
