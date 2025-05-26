import { useParams } from "react-router";
import { Button, Card, Divider, Group, Stack, Tabs, Text, TextInput, Title } from "@mantine/core";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";
import { z } from "zod";
import { Controller, useForm, UseFormReturn } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { enqueueSnackbar } from "notistack";
import { InviteeListTable } from "@/features/organizations/components/InviteeListTable.tsx";
import { useCreateInviteMutation } from "@/features/organizations/apis/createInvite.ts";
import { MemberListTable } from "@/features/organizations/components/MemberListTable.tsx";
import { useState } from "react";
import { DeleteMemberConfirmation } from "@/features/organizations/components/DeleteMemberConfirmation.tsx";
import { UpdateMemberModal } from "@/features/organizations/components/UpdateMemberModal.tsx";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";
import { OrganizationMemberRole } from "@/features/organizations/types";
import { useDeleteOrganizationMembershipMutation } from "@/features/organizations/apis/deleteOrganizationMember.ts";

const InviteFormSchema = z.object({
  email: z.string().email("Invalid email address"),
});

type InviteFormValues = z.infer<typeof InviteFormSchema>;

interface UseInviteFormReturn {
  formMethods: UseFormReturn<InviteFormValues>;
  onSubmit: () => void;
}

const useInviteForm = (organizationId: string): UseInviteFormReturn => {
  const formMethods = useForm<InviteFormValues>({
    defaultValues: {
      email: "",
    },
    resolver: zodResolver(InviteFormSchema),
  });
  const { handleSubmit, reset } = formMethods;
  const createInviteMutation = useCreateInviteMutation();

  const onSubmit = handleSubmit(
    (data) => {
      if (!organizationId) {
        return;
      }

      createInviteMutation.mutate(
        { organizationId: organizationId, email: data.email },
        {
          onSuccess: () => {
            enqueueSnackbar(`Invite sent to ${data.email}`, { variant: "success" });
            reset();
          },
          onError: (error) => {
            enqueueSnackbar(`Failed to send invite: ${error.message}`, { variant: "error" });
          },
        },
      );
    },
    (errors) => {
      console.debug("Form errors:", errors);
    },
  );

  return {
    formMethods: formMethods,
    onSubmit,
  };
};

const InviteForm = () => {
  const { organizationId = "" } = useParams();
  const { formMethods, onSubmit } = useInviteForm(organizationId);
  const { control } = formMethods;

  return (
    <form onSubmit={onSubmit}>
      <Group align="flex-end">
        <Controller
          control={control}
          name="email"
          render={({ field, fieldState }) => (
            <TextInput {...field} label="Email" error={fieldState.error?.message} required />
          )}
        />
        <Button type="submit">Send</Button>
      </Group>
    </form>
  );
};

const InvitesTabContent = () => {
  return (
    <Stack>
      <Title order={3}>New Invite</Title>
      <InviteForm />
      <Divider />
      <Title order={3}>Pending Invites</Title>
      <InviteeListTable />
    </Stack>
  );
};

const MembersTabContent = (
  props: Readonly<{
    currentUserRole: OrganizationMemberRole;
  }>,
) => {
  const { currentUserRole } = props;
  const { organizationId = "" } = useParams();
  const [editMemberId, setEditMemberId] = useState<string | null>(null);
  const [deleteMemberId, setDeleteMemberId] = useState<string | null>(null);
  const organizationDetailQuery = useOrganizationDetailQuery(organizationId);
  const members = organizationDetailQuery.data?.members || [];
  const deleteOrganizationMembershipMutation = useDeleteOrganizationMembershipMutation();

  const onDelete = (userId: string) => {
    if (deleteOrganizationMembershipMutation.isPending) {
      return;
    }

    deleteOrganizationMembershipMutation.mutate(
      {
        organizationId: organizationId,
        userId: userId,
      },
      {
        onSuccess: () => {
          enqueueSnackbar(`Member removed successfully`, { variant: "success" });
          setDeleteMemberId(null);
        },
        onError: (error) => {
          enqueueSnackbar(`Failed to remove member: ${error.message}`, { variant: "error" });
        },
      },
    );
  };
  const deleteConfirmationUser = members.find((member) => member.userId === deleteMemberId);

  return (
    <>
      <MemberListTable
        onEditClick={(userId) => setEditMemberId(userId)}
        onDeleteClick={(userId) => setDeleteMemberId(userId)}
        showActions={currentUserRole === "OWNER"}
      />
      <DeleteMemberConfirmation
        user={{
          id: deleteConfirmationUser?.userId || "",
          firstName: deleteConfirmationUser?.firstName || "",
          lastName: deleteConfirmationUser?.lastName || "",
        }}
        opened={!!deleteMemberId}
        onDelete={onDelete}
        onClose={() => setDeleteMemberId(null)}
      />
      <UpdateMemberModal
        opened={!!editMemberId}
        organizationId={organizationId}
        userId={editMemberId || ""}
        onClose={() => setEditMemberId(null)}
      />
    </>
  );
};

export const OrganizationDetail = () => {
  const { organizationId } = useParams();
  const organizationDetailQuery = useOrganizationDetailQuery(organizationId || "");
  const userDetailQuery = useCurrentUserDetailsQuery();

  if (organizationDetailQuery.isLoading || userDetailQuery.isLoading) {
    return <Text>Loading...</Text>;
  } else if (organizationDetailQuery.isError || userDetailQuery.isError) {
    return <Text>Error loading org</Text>;
  }
  const currentUserMembership = organizationDetailQuery.data?.members.find(
    (member) => member.userId === userDetailQuery.data?.id,
  );

  return (
    <>
      <Card>
        <Tabs defaultValue="general">
          <Tabs.List mb="1.5rem">
            <Tabs.Tab value="general">General</Tabs.Tab>
            <Tabs.Tab value="members">Members</Tabs.Tab>
            {currentUserMembership?.role === "OWNER" && (
              <>
                <Tabs.Tab value="invites">Invites</Tabs.Tab>
                <Tabs.Tab value="danger">Danger</Tabs.Tab>
              </>
            )}
          </Tabs.List>

          <Tabs.Panel value="members">
            <MembersTabContent currentUserRole={currentUserMembership?.role || "MEMBER"} />
          </Tabs.Panel>
          <Tabs.Panel value="invites">
            <InvitesTabContent />
          </Tabs.Panel>

          <Tabs.Panel value="danger">
            <Button variant="outline" color="danger">
              Delete Organization
            </Button>
          </Tabs.Panel>
        </Tabs>
      </Card>
    </>
  );
};
