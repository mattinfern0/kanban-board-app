import { client } from "@/lib/backendApi.ts";
import { CreateInviteBody, OrganizationInvitee } from "@/features/organizations/types";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const createInvite = (body: CreateInviteBody): Promise<OrganizationInvitee[]> => {
  return client
    .post(`invites`, {
      json: body,
    })
    .json();
};

export const useCreateInviteMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: CreateInviteBody) => createInvite(body),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["organizations", variables.organizationId, "invitees"],
      });
    },
  });
};
