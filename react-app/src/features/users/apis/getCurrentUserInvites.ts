import { InviteDetail } from "@/features/users/types";
import { client } from "@/lib/backendApi.ts";
import { useQuery } from "@tanstack/react-query";

export const getCurrentUserInvites = async (): Promise<InviteDetail[]> => {
  return await client.get("users/me/invites").json();
};

export const useCurrentUserInvitesQuery = () => {
  return useQuery({
    queryFn: async () => {
      return await getCurrentUserInvites();
    },
    queryKey: ["users", "me", "invites"],
  });
};
