import { Container } from "@mui/material";
import React from "react";
import { SnackbarProvider } from "notistack";

interface UnauthenticatedLayoutProps {
  children: React.ReactNode;
}

export const UnauthenticatedLayout = ({ children }: UnauthenticatedLayoutProps) => {
  return (
    <>
      <SnackbarProvider>
        <Container sx={{ paddingTop: "3rem" }} maxWidth="md">
          {children}
        </Container>
      </SnackbarProvider>
    </>
  );
};
