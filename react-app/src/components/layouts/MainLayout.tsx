import { AppBar, Avatar, Container, IconButton, Stack, Toolbar, Typography } from "@mui/material";
import React from "react";
import { AppSidebar } from "@/components/navs/AppSidebar.tsx";
import { SnackbarProvider } from "notistack";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  return (
    <>
      <SnackbarProvider>
        <AppBar sx={{ zIndex: 1300 }}>
          <Toolbar>
            <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ width: "100%" }}>
              <Typography variant="h6">Kanban App</Typography>
              <IconButton>
                <Avatar />
              </IconButton>
            </Stack>
          </Toolbar>
        </AppBar>
        <Toolbar />
        <AppSidebar />
        <Container sx={{ paddingTop: "3rem" }}>{children}</Container>
      </SnackbarProvider>
    </>
  );
};
