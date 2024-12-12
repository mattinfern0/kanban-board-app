import React from "react";
import { AppSidebar } from "@/components/navs/AppSidebar.tsx";
import { SnackbarProvider } from "notistack";
import { AppShell, Avatar, Group, Title, useMantineTheme } from "@mantine/core";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  const theme = useMantineTheme();
  return (
    <>
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
              <Title variant="h6">Kanban App</Title>
              <Avatar />
            </Group>
          </AppShell.Header>
          <AppShell.Navbar p="md">
            <AppSidebar />
          </AppShell.Navbar>
          <AppShell.Main>{children}</AppShell.Main>
        </AppShell>
      </SnackbarProvider>
    </>
  );
};
