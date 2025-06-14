import { RouteObject } from "react-router";
import { MainLayout } from "@/components/layouts/MainLayout.tsx";
import { BoardView } from "@/features/boards/routes/BoardView.tsx";
import { BoardListView } from "@/features/boards/routes/BoardListView.tsx";
import { BoardSettings } from "@/features/boards/routes/BoardSettings.tsx";
import { UnauthenticatedLayout } from "@/components/layouts/UnauthenticatedLayout.tsx";
import { SignUpView } from "@/features/auth/routes/SignUpView.tsx";
import { TaskListView } from "@/features/tasks/routes/TaskListView.tsx";
import { LoginView } from "@/features/auth/routes/LoginView.tsx";
import { AuthGuard } from "@/features/auth/components/AuthGuard.tsx";
import { IndexView } from "@/routes/IndexView.tsx";
import { SwitchOrganizationView } from "@/features/organizations/routes/SwitchOrganizationView.tsx";
import { OrganizationDetail } from "@/features/organizations/routes/OrganizationDetail.tsx";
import { UserAccount } from "@/features/users/routes/UserAccount.tsx";

export const routes: RouteObject[] = [
  {
    path: "/sign-up",
    element: (
      <UnauthenticatedLayout>
        <SignUpView />
      </UnauthenticatedLayout>
    ),
  },
  {
    path: "/login",
    element: (
      <UnauthenticatedLayout>
        <LoginView />
      </UnauthenticatedLayout>
    ),
  },
  {
    path: "/",
    element: (
      <AuthGuard>
        <IndexView />
      </AuthGuard>
    ),
  },
  {
    path: "/my/organizations",
    element: (
      <MainLayout>
        <SwitchOrganizationView />
      </MainLayout>
    ),
  },
  {
    path: "/my/account",
    element: (
      <MainLayout showSidebar={false}>
        <UserAccount />
      </MainLayout>
    ),
  },
  {
    path: "/:organizationId",
    children: [
      {
        path: "tasks",
        element: (
          <MainLayout>
            <TaskListView />
          </MainLayout>
        ),
      },
      {
        path: "boards",
        element: (
          <MainLayout>
            <BoardListView />
          </MainLayout>
        ),
      },
      {
        path: "boards/:boardId",
        element: (
          <MainLayout>
            <BoardView />
          </MainLayout>
        ),
      },
      {
        path: "boards/:boardId/settings",
        element: (
          <MainLayout>
            <BoardSettings />
          </MainLayout>
        ),
      },
      {
        path: "settings",
        element: (
          <MainLayout>
            <OrganizationDetail />
          </MainLayout>
        ),
      },
    ],
  },
];
