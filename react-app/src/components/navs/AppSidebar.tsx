import { Link } from "react-router";
import { NavLink } from "@mantine/core";
import { IconColumns3, IconListCheck } from "@tabler/icons-react";

export const AppSidebar = () => {
  return (
    <>
      <NavLink component={Link} to="/boards" label="Boards" leftSection={<IconColumns3 size="1rem" stroke={1.5} />} />
      <NavLink component={Link} to="/tasks" label="Tasks" leftSection={<IconListCheck size="1rem" stroke={1.5} />} />
    </>
  );
};
