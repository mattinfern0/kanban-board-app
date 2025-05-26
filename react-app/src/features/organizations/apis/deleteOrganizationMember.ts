import { client } from "@/lib/backendApi.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface UpdateOrganizationMemberArgs {
  organizationId: string;
  userId: string;
}

export const deleteOrganizationMember = (args: UpdateOrganizationMemberArgs): Promise<void> => {
  return client.delete(`organizations/${args.organizationId}/members/${args.userId}`, {}).json();
};

export const useDeleteOrganizationMembershipMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (args: UpdateOrganizationMemberArgs) => deleteOrganizationMember(args),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["organizations", variables.organizationId],
      });
    },
  });
};
