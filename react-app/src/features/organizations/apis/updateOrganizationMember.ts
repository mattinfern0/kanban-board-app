import { client } from "@/lib/backendApi.ts";
import { OrganizationMemberRole } from "@/features/organizations/types";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface UpdateOrganizationMemberArgs {
  organizationId: string;
  userId: string;
  role: OrganizationMemberRole;
}

export const updateOrganizationMember = (args: UpdateOrganizationMemberArgs): Promise<void> => {
  return client
    .put(`organizations/${args.organizationId}/members/${args.userId}`, {
      json: {
        role: args.role,
      },
    })
    .json();
};

export const useUpdateOrganizationMembershipMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (args: UpdateOrganizationMemberArgs) => updateOrganizationMember(args),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["organizations", variables.organizationId],
      });
    },
  });
};
