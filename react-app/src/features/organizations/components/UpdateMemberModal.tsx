import { Avatar, Button, Group, Modal, Select, Stack, Text, Title } from "@mantine/core";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";
import { useEffect } from "react";
import { z } from "zod";
import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { usePrevious } from "@/lib/hooks.ts";
import deepEqual from "deep-equal";
import { useUpdateOrganizationMembershipMutation } from "@/features/organizations/apis/updateOrganizationMember.ts";
import { enqueueSnackbar } from "notistack";

export interface UpdateMemberModalProps {
  opened: boolean;
  organizationId: string;
  userId: string;
  onClose: () => void;
}

const UpdateMemberFormSchema = z.object({
  role: z.enum(["OWNER", "MEMBER"]),
});

type UpdateMemberFormValues = z.infer<typeof UpdateMemberFormSchema>;

export const UpdateMemberModal = (props: Readonly<UpdateMemberModalProps>) => {
  const { opened, onClose, organizationId, userId } = props;
  const organizationDetailQuery = useOrganizationDetailQuery(organizationId);
  const user = organizationDetailQuery.data?.members.find((member) => member.userId === userId);

  const updateOrganizationMembershipMutation = useUpdateOrganizationMembershipMutation();

  const updateMemberFormMethods = useForm<UpdateMemberFormValues>({
    resolver: zodResolver(UpdateMemberFormSchema),
    defaultValues: {
      role: user?.role || "MEMBER",
    },
  });
  const { handleSubmit, control, reset } = updateMemberFormMethods;

  const previousUser = usePrevious(user);
  useEffect(() => {
    if (!deepEqual(previousUser, user)) {
      reset({
        role: user?.role || "MEMBER",
      });
    }
  }, [previousUser, user, reset]);

  const onSubmit = handleSubmit(
    (data) => {
      console.debug("Updated member data:", data);
      if (updateOrganizationMembershipMutation.isPending) {
        return;
      }

      updateOrganizationMembershipMutation.mutate(
        {
          organizationId: organizationId,
          userId: userId,
          role: data.role,
        },
        {
          onSuccess: () => {
            enqueueSnackbar(`Member updated successfully`, { variant: "success" });
          },
          onError: (error) => {
            enqueueSnackbar(`Failed to update member`, { variant: "error" });
            console.error("Error updating member:", error);
          },
        },
      );
    },
    (errors) => {
      console.debug("Form errors:", errors);
    },
  );

  let modalContent = <Text>Loading...</Text>;
  if (user) {
    const userFullName = `${user.firstName} ${user.lastName}`;
    const userAvatar = <Avatar size="lg" name={userFullName} color="initials" />;
    modalContent = (
      <Stack>
        <Group>
          {userAvatar}
          <Title>{userFullName}</Title>
        </Group>
        <form onSubmit={onSubmit}>
          <Stack>
            <Controller
              control={control}
              name="role"
              render={({ field }) => (
                <Select
                  {...field}
                  label="Role"
                  data={[
                    { value: "OWNER", label: "Owner" },
                    { value: "MEMBER", label: "Member" },
                  ]}
                />
              )}
            />
            <Group justify="flex-end">
              <Button type="submit">Save</Button>
            </Group>
          </Stack>
        </form>
      </Stack>
    );
  }

  return (
    <Modal opened={opened} onClose={onClose} size="lg">
      {modalContent}
    </Modal>
  );
};
