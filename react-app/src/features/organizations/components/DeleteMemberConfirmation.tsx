import { Button, Group, Modal, Text } from "@mantine/core";

export interface DeleteMemberConfirmationUserInfo {
  id: string;
  firstName: string;
  lastName: string;
}

export interface DeleteMemberConfirmationProps {
  opened: boolean;
  user: DeleteMemberConfirmationUserInfo;
  onDelete: (userId: string) => void;
  onClose: () => void;
}

export const DeleteMemberConfirmation = (props: DeleteMemberConfirmationProps) => {
  const { user, opened, onDelete, onClose } = props;

  const handleDelete = () => {
    onDelete(user.id);
  };

  const userFullName = `${user.firstName} ${user.lastName}`;

  return (
    <Modal opened={opened} onClose={onClose} title="Confirm" withCloseButton={false} size="auto">
      <Text>Are you sure you want to remove {userFullName} from the organization?</Text>
      <Text size="sm" c="dimmed">
        Note: Tasks assigned to this user will have to be manually reassigned.
      </Text>
      <Group justify="flex-end" mt="md">
        <Button color="secondary" variant="outline" onClick={onClose}>
          Cancel
        </Button>
        <Button color="red" variant="outline" onClick={handleDelete}>
          Delete
        </Button>
      </Group>
    </Modal>
  );
};
