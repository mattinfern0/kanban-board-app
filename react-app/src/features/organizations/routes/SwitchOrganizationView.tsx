import { Avatar, Box, Card, Divider, Group, Stack, Text } from "@mantine/core";
import { ReactNode } from "react";
import { Link } from "react-router";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";

export const SwitchOrganizationView = () => {
  // const params = useParams();

  const userDetailsQuery = useCurrentUserDetailsQuery();

  // const currentOrganizationId = params?.organizationId || null;

  const isPending = userDetailsQuery.isPending;
  const isError = userDetailsQuery.isError;

  let listElement: ReactNode;
  if (isPending) {
    listElement = <Text>Loading...</Text>;
  } else if (isError) {
    listElement = <Text>Error loading organizations</Text>;
  } else {
    const personalOrganization = userDetailsQuery.data?.organizations.find(
      (organization) => organization.id === userDetailsQuery.data?.personalOrganizationId,
    );

    const fullName = `${userDetailsQuery.data.firstName} ${userDetailsQuery.data.lastName}`;
    const userAvatar = <Avatar name={fullName} color="initials" />;

    const personalOrganizationCard = (
      <Card
        key={personalOrganization?.id}
        component={Link}
        to={`/${personalOrganization?.id}/boards`}
        style={{ cursor: "pointer" }}
        withBorder
      >
        <Group>
          {userAvatar}
          <Box>
            <Text size="xl">Your Space</Text>
            <Text size="sm">A private space for your work</Text>
          </Box>
        </Group>
      </Card>
    );

    const nonPersonalOrganizations =
      userDetailsQuery.data?.organizations.filter(
        (organization) => organization.id !== userDetailsQuery.data?.personalOrganizationId,
      ) || [];

    const nonPersonalOrganizationCards = nonPersonalOrganizations.map((organization) => (
      <Card
        key={organization.id}
        component={Link}
        to={`/${organization.id}/boards`}
        style={{ cursor: "pointer" }}
        withBorder
      >
        <Text size="xl">{organization.displayName}</Text>
      </Card>
    ));
    listElement = (
      <Stack gap="md" style={{ padding: "1rem" }}>
        {personalOrganizationCard}
        <Divider my="md" />
        <Stack>{nonPersonalOrganizationCards}</Stack>
      </Stack>
    );
  }

  return listElement;
};
