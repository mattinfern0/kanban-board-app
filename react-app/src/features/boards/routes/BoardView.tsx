import { useBoardQuery } from "../apis/getBoard.ts";
import { Link, useParams } from "react-router";
import { useState } from "react";
import { BoardTask } from "@/features/boards/types";
import { TaskDetailModal } from "@/features/tasks/components/TaskDetailModal.tsx";
import { CreateTaskModal } from "@/features/tasks/components/CreateTaskModal.tsx";
import { BoardWorkspace } from "@/features/boards/components/BoardWorkspace.tsx";
import { ActionIcon, Breadcrumbs, Button, Group, Text, Title } from "@mantine/core";
import { IconSettingsFilled } from "@tabler/icons-react";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";

export const BoardView = () => {
  const { boardId = "", organizationId = "" } = useParams();
  const boardQuery = useBoardQuery(boardId);
  const organizationDetailQuery = useOrganizationDetailQuery(organizationId);
  const userDetailQuery = useCurrentUserDetailsQuery();
  const [showTaskDialog, setShowTaskDialog] = useState<boolean>(false);
  const [taskDialogTaskId, setTaskDialogTaskId] = useState<string | null>(null);
  const [showCreateTaskDialog, setShowCreateTaskDialog] = useState<boolean>(false);

  if (boardQuery.isPending) {
    return <Title>Loading...</Title>;
  }

  if (boardQuery.isError) {
    return <Title>Error!</Title>;
  }

  const userMembership = organizationDetailQuery.data?.members?.find(
    (member) => member.userId === userDetailQuery.data?.id,
  );

  const showSettingsButton = userMembership && userMembership.role === "OWNER";

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
          <Text component={Link} to={`/${organizationId}/boards`}>
            Boards
          </Text>
          <Title order={2}>{boardQuery.data.title}</Title>
        </Breadcrumbs>

        <Group>
          <Button variant="filled" onClick={() => setShowCreateTaskDialog(true)} color="primary">
            Create Task
          </Button>

          {showSettingsButton && (
            <ActionIcon
              color="secondary"
              component={Link}
              to={`/${organizationId}/boards/${boardId}/settings`}
              variant="outline"
              aria-label="Board Settings"
              size="lg"
            >
              <IconSettingsFilled />
            </ActionIcon>
          )}
        </Group>
      </Group>

      <BoardWorkspace boardId={boardId} handleTaskCardClick={onTaskCardClick} />
    </>
  );
};
