import { client } from "@/lib/backendApi.ts";
import { OrganizationInvitee } from "@/features/organizations/types";
import { useQuery } from "@tanstack/react-query";

export const getOrganizationInvitees = (organizationId: string): Promise<OrganizationInvitee[]> => {
  return client.get(`organizations/${organizationId}/invitees`).json();
};

export const useOrganizationInviteesQuery = (organizationId: string, options?: { enabled: boolean }) => {
  return useQuery({
    queryFn: () => getOrganizationInvitees(organizationId),
    queryKey: ["organizations", organizationId, "invitees"],
    enabled: options ? options.enabled : true,
  });
};
