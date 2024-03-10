import { AppBar, Container, Toolbar, Typography } from "@mui/material";
import React from "react";

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  return (
    <>
      <AppBar position="static" p>
        <Toolbar>
          <Typography variant="h6">Kanban App</Typography>
        </Toolbar>
      </AppBar>
      <Container>{children}</Container>
    </>
  );
};
