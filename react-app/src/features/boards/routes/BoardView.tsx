import { useBoardQuery } from "../apis/getBoard.ts";
import { Button, IconButton, Stack, Typography } from "@mui/material";
import { Link, useParams } from "react-router-dom";
import { useState } from "react";
import { BoardTask } from "@/features/boards/types";
import { BoardTaskDetail } from "@/features/boards/components/BoardTaskDetail.tsx";
import { CreateTaskDialog } from "@/features/tasks/components/CreateTaskDialog.tsx";
import { Settings } from "@mui/icons-material";
import { BoardColumnWorkspace } from "@/features/boards/components/BoardColumnWorkspace.tsx";

export const BoardView = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [showTaskDialog, setShowTaskDialog] = useState<boolean>(false);
  const [taskDialogTaskId, setTaskDialogTaskId] = useState<string | null>(null);
  const [showCreateTaskDialog, setShowCreateTaskDialog] = useState<boolean>(false);

  if (boardQuery.isPending) {
    return <Typography>Loading...</Typography>;
  }

  if (boardQuery.isError) {
    return <Typography>Error!</Typography>;
  }

  const board = boardQuery.data;

  const onTaskCardClick = (task: BoardTask) => {
    setTaskDialogTaskId(task.id);
    setShowTaskDialog(true);
  };

  return (
    <>
      <BoardTaskDetail
        open={showTaskDialog}
        taskId={taskDialogTaskId}
        organizationId={board.organizationId}
        onClose={() => {
          setShowTaskDialog(false);
        }}
      />
      <CreateTaskDialog
        open={showCreateTaskDialog}
        onClose={() => setShowCreateTaskDialog(false)}
        organizationId={board.organizationId}
        boardId={board.id}
      />

      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" mb={0}>
          {boardQuery.data.title}
        </Typography>

        <Stack direction="row" spacing={3}>
          <Button variant="contained" onClick={() => setShowCreateTaskDialog(true)}>
            Create Task
          </Button>
          <IconButton component={Link} to={`/boards/${boardId}/settings`}>
            <Settings />
          </IconButton>
        </Stack>
      </Stack>

      <BoardColumnWorkspace board={board} handleTaskCardClick={onTaskCardClick} />
    </>
  );
};
