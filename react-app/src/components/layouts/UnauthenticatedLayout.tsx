import { Container } from "@mantine/core";
import React from "react";
import { SnackbarProvider } from "notistack";

interface UnauthenticatedLayoutProps {
  children: React.ReactNode;
}

export const UnauthenticatedLayout = ({ children }: UnauthenticatedLayoutProps) => {
  return (
    <>
      <SnackbarProvider>
        <Container pt="3rem" size="xs">
          {children}
        </Container>
      </SnackbarProvider>
    </>
  );
};
