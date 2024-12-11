import { Link } from "react-router-dom";
import { NavLink } from "@mantine/core";

export const AppSidebar = () => {
  return (
    <>
      <NavLink component={Link} to="/boards" label="Boards" />
      <NavLink component={Link} to="#" label="Tasks" />
    </>
  );
};
