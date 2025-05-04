import { Link, useParams } from "react-router";
import { NavLink } from "@mantine/core";
import { IconColumns3, IconListCheck, IconUsersGroup } from "@tabler/icons-react";

export const AppSidebar = () => {
  const params = useParams();

  const organizationId = params.organizationId ?? "";

  return (
    <>
      <NavLink
        component={Link}
        to={`#`}
        label="Organizations"
        leftSection={<IconUsersGroup size="1rem" stroke={1.5} />}
      />
      <NavLink
        component={Link}
        to={`/${organizationId}/boards`}
        label="Boards"
        leftSection={<IconColumns3 size="1rem" stroke={1.5} />}
      />
      <NavLink
        component={Link}
        to={`/${organizationId}/tasks`}
        label="Tasks"
        leftSection={<IconListCheck size="1rem" stroke={1.5} />}
      />
    </>
  );
};
