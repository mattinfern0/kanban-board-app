import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { routes } from "@/routes";
import { CssBaseline } from "@mui/material";

const router = createBrowserRouter(routes);

const App = () => {
  return (
    <>
      <CssBaseline />
      <RouterProvider router={router} />
    </>
  );
};

export default App;
