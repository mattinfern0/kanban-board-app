import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { routes } from "@/routes";
import { CssBaseline } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const router = createBrowserRouter(routes);
const queryClient = new QueryClient();

const App = () => {
  return (
    <>
      <CssBaseline />
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
      </QueryClientProvider>
    </>
  );
};

export default App;
