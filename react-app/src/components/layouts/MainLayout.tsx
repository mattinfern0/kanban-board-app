import { AppBar, Container, Toolbar, Typography } from "@mui/material";
import React from "react";
import { AppSidebar } from "@/components/navs/AppSidebar.tsx";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  return (
    <>
      <AppBar position="static" sx={{ zIndex: 1300 }}>
        <Toolbar>
          <Typography variant="h6">Kanban App</Typography>
        </Toolbar>
      </AppBar>
      <Toolbar />
      <AppSidebar />
      <Container>{children}</Container>
    </>
  );
};
