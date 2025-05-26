import { client } from "@/lib/backendApi.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const revokeInvite = (inviteId: string): Promise<void> => {
  return client.post(`invites/${inviteId}/revoke`, {}).json();
};

export const useRevokeInviteMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (inviteId: string) => revokeInvite(inviteId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        // TODO : This should be more specific to the organization invitees
        queryKey: ["organizations"],
      });
      queryClient.invalidateQueries({
        queryKey: ["users", "me", "invites"],
      });
    },
  });
};
