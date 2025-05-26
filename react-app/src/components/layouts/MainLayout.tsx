import React from "react";
import { AppSidebar } from "@/components/navs/AppSidebar.tsx";
import { SnackbarProvider } from "notistack";
import { AppShell, useMantineTheme } from "@mantine/core";
import { AuthGuard } from "@/features/auth/components/AuthGuard.tsx";
import { AppHeader } from "@/components/navs/AppHeader.tsx";

interface MainLayoutProps {
  children: React.ReactNode;
  showSidebar?: boolean;
}

export const MainLayout = ({ children, showSidebar = true }: MainLayoutProps) => {
  const theme = useMantineTheme();

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
            main: {
              backgroundColor: theme.colors.corkBrown[2],
            },
          }}
        >
          <AppShell.Header>
            <AppHeader />
          </AppShell.Header>
          {showSidebar && (
            <AppShell.Navbar p="md">
              <AppSidebar />
            </AppShell.Navbar>
          )}
          <AppShell.Main>{children}</AppShell.Main>
        </AppShell>
      </SnackbarProvider>
    </AuthGuard>
  );
};
