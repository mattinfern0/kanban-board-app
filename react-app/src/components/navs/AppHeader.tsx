import { Avatar, Group, Menu, Text, Title, UnstyledButton } from "@mantine/core";
import { Link, useNavigate } from "react-router";
import { useAuth } from "@/features/auth/components/AuthProvider.tsx";
import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";

export const AppHeader = () => {
  const auth = useAuth();
  const navigate = useNavigate();

  const currentUserDetailsQuery = useCurrentUserDetailsQuery();

  const onLogoutClick = async () => {
    await auth.logout();
    navigate("/login");
  };

  let userAvatar;
  if (currentUserDetailsQuery.isLoading || currentUserDetailsQuery.isError || !currentUserDetailsQuery.data) {
    userAvatar = <Avatar component={UnstyledButton} variant="filled" />;
  } else {
    const fullName = `${currentUserDetailsQuery.data.firstName} ${currentUserDetailsQuery.data.lastName}`;
    userAvatar = <Avatar name={fullName} color="initials" component={UnstyledButton} />;
  }

  return (
    <Group h="100%" justify="space-between" px="md">
      <Group gap="md" align="center">
        <Title order={3}>CorkScreen</Title>
        <Text component={Link} to="/my/organizations" c="white">
          Organizations
        </Text>
      </Group>

      <Menu>
        <Menu.Target>{userAvatar}</Menu.Target>
        <Menu.Dropdown>
          <Menu.Item component={Link} to="/my/account">
            Account
          </Menu.Item>
          <Menu.Item onClick={onLogoutClick}>Logout</Menu.Item>
        </Menu.Dropdown>
      </Menu>
    </Group>
  );
};
