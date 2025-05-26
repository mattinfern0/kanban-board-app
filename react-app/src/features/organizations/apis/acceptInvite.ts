import { client } from "@/lib/backendApi.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const acceptInvite = (inviteToken: string): Promise<void> => {
  return client
    .post(`invites/accept`, {
      json: {
        token: inviteToken,
      },
    })
    .json();
};

export const useAcceptInviteMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (inviteToken: string) => acceptInvite(inviteToken),
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
