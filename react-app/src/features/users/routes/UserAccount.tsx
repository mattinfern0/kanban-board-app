import { Avatar, Badge, Card, Group, Stack, Tabs, Text, Title } from "@mantine/core";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";
import { OrganizationInviteListTable } from "@/features/users/components/OrganizationInviteListTable.tsx";
import { useCurrentUserInvitesQuery } from "@/features/users/apis/getCurrentUserInvites.ts";

const InviteBadge = () => {
  const currentUserInvitesQuery = useCurrentUserInvitesQuery();

  if (currentUserInvitesQuery.isPending || currentUserInvitesQuery.isError) {
    return null;
  }

  if (currentUserInvitesQuery.data.length === 0) {
    return null;
  }

  return (
    <Badge size="lg" circle>
      {currentUserInvitesQuery.data.length}
    </Badge>
  );
};

const UserAvatar = () => {
  const currentUserDetailsQuery = useCurrentUserDetailsQuery();
  let userAvatar;
  if (currentUserDetailsQuery.isLoading || currentUserDetailsQuery.isError || !currentUserDetailsQuery.data) {
    userAvatar = <Avatar variant="filled" />;
  } else {
    const fullName = `${currentUserDetailsQuery.data.firstName} ${currentUserDetailsQuery.data.lastName}`;
    userAvatar = <Avatar name={fullName} color="initials" />;
  }

  return userAvatar;
};

export const UserAccount = () => {
  const userDetailQuery = useCurrentUserDetailsQuery();

  if (userDetailQuery.isLoading) {
    return <Text>Loading...</Text>;
  } else if (userDetailQuery.isError) {
    return <Text>Error loading org</Text>;
  }

  const userFullName = `${userDetailQuery.data?.firstName} ${userDetailQuery.data?.lastName}`;

  return (
    <>
      <Card>
        <Stack>
          <Group align="center">
            <UserAvatar />
            <Title order={2}>{userFullName}</Title>
          </Group>
          <Tabs defaultValue="invites">
            <Tabs.List mb="1.5rem">
              <Tabs.Tab value="invites">
                <Group gap="xs">
                  <Text>Invites</Text>
                  <InviteBadge />
                </Group>
              </Tabs.Tab>
            </Tabs.List>

            <Tabs.Panel value="invites">
              <OrganizationInviteListTable />
            </Tabs.Panel>
          </Tabs>
        </Stack>
      </Card>
    </>
  );
};
