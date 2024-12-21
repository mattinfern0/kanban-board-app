import { createBrowserRouter } from "react-router";
import { RouterProvider } from "react-router/dom";
import { routes } from "@/routes";
import { MantineProvider } from "@mantine/core";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import timezone from "dayjs/plugin/timezone";
import dayjs from "dayjs";

import "@mantine/core/styles.css";
import "mantine-datatable/styles.layer.css";
import "@mantine/tiptap/styles.css";

import { theme } from "@/theme.ts";
import { AuthProvider } from "@/features/auth/components/AuthProvider.tsx";

const router = createBrowserRouter(routes);
const queryClient = new QueryClient();

dayjs.extend(timezone);

const App = () => {
  return (
    <MantineProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </QueryClientProvider>
    </MantineProvider>
  );
};

export default App;
