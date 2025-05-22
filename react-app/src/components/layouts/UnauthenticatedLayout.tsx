import { Container, Grid, useMantineTheme } from "@mantine/core";
import React from "react";
import { SnackbarProvider } from "notistack";

interface UnauthenticatedLayoutProps {
  children: React.ReactNode;
}

export const UnauthenticatedLayout = ({ children }: UnauthenticatedLayoutProps) => {
  const theme = useMantineTheme();
  return (
    <>
      <SnackbarProvider>
        <Container fluid>
          <Grid>
            <Grid.Col
              span={{
                base: 12,
                md: 5,
              }}
              style={{
                height: "100vh",
              }}
            >
              <Container p="3rem">{children}</Container>
            </Grid.Col>
            <Grid.Col
              span={{
                base: 0,
                md: 7,
              }}
              style={{
                backgroundColor: theme.colors.corkBrown[3],
                height: "100vh",
              }}
            ></Grid.Col>
          </Grid>
        </Container>
      </SnackbarProvider>
    </>
  );
};
