import { UserPrivateDetail } from "@/features/users/types";
import { client } from "@/lib/backendApi.ts";
import { useQuery } from "@tanstack/react-query";

export const getCurrentUserDetails = async (): Promise<UserPrivateDetail> => {
  return await client.get("users/me").json();
};

export const useCurrentUserDetailsQuery = () => {
  return useQuery({
    queryFn: async () => {
      return await getCurrentUserDetails();
    },
    queryKey: ["users", "me"],
  });
};
