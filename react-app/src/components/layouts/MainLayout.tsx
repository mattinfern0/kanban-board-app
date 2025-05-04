import React from "react";
import { AppSidebar } from "@/components/navs/AppSidebar.tsx";
import { SnackbarProvider } from "notistack";
import { AppShell, Avatar, Group, Menu, Title, UnstyledButton, useMantineTheme } from "@mantine/core";
import { useNavigate } from "react-router";
import { useAuth } from "@/features/auth/components/AuthProvider.tsx";
import { AuthGuard } from "@/features/auth/components/AuthGuard.tsx";
import { useGetCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  const theme = useMantineTheme();

  const auth = useAuth();
  const navigate = useNavigate();

  const currentUserDetailsQuery = useGetCurrentUserDetailsQuery();

  const onLogoutClick = async () => {
    await auth.logout();
    navigate("/login");
  };

  let userAvatar = null;
  if (currentUserDetailsQuery.isLoading || currentUserDetailsQuery.isError || !currentUserDetailsQuery.data) {
    userAvatar = <Avatar component={UnstyledButton} variant="filled" />;
  } else {
    const fullName = `${currentUserDetailsQuery.data.firstName} ${currentUserDetailsQuery.data.lastName}`;
    userAvatar = <Avatar name={fullName} color="initials" component={UnstyledButton} />;
  }

  return (
    <AuthGuard>
      <SnackbarProvider>
        <AppShell
          header={{ height: 60 }}
          navbar={{ width: 200, breakpoint: "sm" }}
          padding="md"
          styles={{
            header: {
              backgroundColor: theme.colors.blueTeal[4],
            },
          }}
        >
          <AppShell.Header>
            <Group h="100%" justify="space-between" px="md">
              <Title order={3}>Kanban App</Title>
              <Menu>
                <Menu.Target>{userAvatar}</Menu.Target>
                <Menu.Dropdown>
                  <Menu.Item onClick={onLogoutClick}>Logout</Menu.Item>
                </Menu.Dropdown>
              </Menu>
            </Group>
          </AppShell.Header>
          <AppShell.Navbar p="md">
            <AppSidebar />
          </AppShell.Navbar>
          <AppShell.Main>{children}</AppShell.Main>
        </AppShell>
      </SnackbarProvider>
    </AuthGuard>
  );
};
