import { Link, useParams } from "react-router";
import { NavLink } from "@mantine/core";
import { IconColumns3, IconListCheck, IconUsersGroup } from "@tabler/icons-react";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";

export const AppSidebar = () => {
  const { organizationId = "" } = useParams();

  const organizationDetalQuery = useOrganizationDetailQuery(organizationId);

  const isPersonal = organizationDetalQuery.data?.isPersonal || false;

  return (
    <>
      {organizationDetalQuery.data && (
        <>
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
          {!isPersonal && (
            <NavLink
              component={Link}
              to={`/${organizationId}/settings`}
              label="Org Settings"
              leftSection={<IconUsersGroup size="1rem" stroke={1.5} />}
            />
          )}
        </>
      )}
    </>
  );
};
