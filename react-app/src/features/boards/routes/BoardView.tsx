import { useBoardQuery } from "../apis/getBoard.ts";
import { Link, useParams } from "react-router";
import { useState } from "react";
import { BoardTask } from "@/features/boards/types";
import { TaskDetailModal } from "@/features/tasks/components/TaskDetailModal.tsx";
import { CreateTaskModal } from "@/features/tasks/components/CreateTaskModal.tsx";
import { BoardColumnWorkspace } from "@/features/boards/components/BoardColumnWorkspace.tsx";
import { ActionIcon, Breadcrumbs, Button, Group, Text, Title } from "@mantine/core";
import { IconSettingsFilled } from "@tabler/icons-react";

export const BoardView = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [showTaskDialog, setShowTaskDialog] = useState<boolean>(false);
  const [taskDialogTaskId, setTaskDialogTaskId] = useState<string | null>(null);
  const [showCreateTaskDialog, setShowCreateTaskDialog] = useState<boolean>(false);

  if (boardQuery.isPending) {
    return <Title>Loading...</Title>;
  }

  if (boardQuery.isError) {
    return <Title>Error!</Title>;
  }

  const board = boardQuery.data;

  const onTaskCardClick = (task: BoardTask) => {
    setTaskDialogTaskId(task.id);
    setShowTaskDialog(true);
  };

  return (
    <>
      <TaskDetailModal
        open={showTaskDialog}
        taskId={taskDialogTaskId}
        onClose={() => {
          setShowTaskDialog(false);
        }}
      />
      <CreateTaskModal
        open={showCreateTaskDialog}
        onClose={() => setShowCreateTaskDialog(false)}
        organizationId={board.organizationId}
        boardId={board.id}
      />

      <Group justify="space-between" align="center" mb="1rem">
        <Breadcrumbs>
          <Text component={Link} to="/boards">
            Boards
          </Text>
          <Title order={2}>{boardQuery.data.title}</Title>
        </Breadcrumbs>

        <Group>
          <Button variant="filled" onClick={() => setShowCreateTaskDialog(true)} color="primary">
            Create Task
          </Button>
          <ActionIcon
            color="secondary"
            component={Link}
            to={`/boards/${boardId}/settings`}
            variant="outline"
            aria-label="Board Settings"
            size="lg"
          >
            <IconSettingsFilled />
          </ActionIcon>
        </Group>
      </Group>

      <BoardColumnWorkspace board={board} handleTaskCardClick={onTaskCardClick} />
    </>
  );
};
