import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { routes } from "@/routes";
import { MantineProvider } from "@mantine/core";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import "@mantine/core/styles.css";
import { theme } from "@/theme.ts";

const router = createBrowserRouter(routes);
const queryClient = new QueryClient();

const App = () => {
  return (
    <MantineProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
      </QueryClientProvider>
    </MantineProvider>
  );
};

export default App;
