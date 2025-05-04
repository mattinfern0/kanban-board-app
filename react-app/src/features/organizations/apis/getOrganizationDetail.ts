import { client } from "@/lib/backendApi.ts";
import { OrganizationDetail } from "@/features/organizations/types";
import { useQuery } from "@tanstack/react-query";

export const getOrganizationDetail = (organizationId: string): Promise<OrganizationDetail> => {
  return client.get(`organizations/${organizationId}`).json();
};

export const useOrganizationDetailQuery = (organizationId: string, options?: { enabled: boolean }) => {
  return useQuery({
    queryFn: () => getOrganizationDetail(organizationId),
    queryKey: ["organizations", organizationId],
    enabled: options?.enabled || true,
  });
};
