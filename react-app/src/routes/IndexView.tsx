import { useCurrentUserDetailsQuery } from "@/features/users/apis/getCurrentUserDetails.ts";
import { Navigate } from "react-router";

export const IndexView = () => {
  const userDetailsQuery = useCurrentUserDetailsQuery();

  if (userDetailsQuery.isPending) {
    return null;
  } else if (userDetailsQuery.isError) {
    throw new Error("Unable to load user details");
  }

  const redirectUrl = `/${userDetailsQuery.data.personalOrganizationId}/boards`;

  return <Navigate to={redirectUrl} replace={true} />;
};
