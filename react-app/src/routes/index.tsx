import { RouteObject } from "react-router-dom";
import { Typography } from "@mui/material";
import { MainLayout } from "@/components/layouts/MainLayout.tsx";

const SamplePage = () => {
  return <Typography>Hello World!</Typography>;
};

export const routes: RouteObject[] = [
  {
    path: "/",
    element: (
      <MainLayout>
        <SamplePage />
      </MainLayout>
    ),
  },
];
