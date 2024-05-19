import { RouteObject } from "react-router-dom";
import { Typography } from "@mui/material";
import { MainLayout } from "@/components/layouts/MainLayout.tsx";
import { BoardView } from "@/features/boards/routes/BoardView.tsx";
import { BoardListView } from "@/features/boards/routes/BoardListView.tsx";

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
  {
    path: "/boards",
    element: (
      <MainLayout>
        <BoardListView />
      </MainLayout>
    ),
  },
  {
    path: "/boards/:boardId",
    element: (
      <MainLayout>
        <BoardView />
      </MainLayout>
    ),
  },
];
