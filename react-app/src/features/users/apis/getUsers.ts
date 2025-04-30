import { GetUsersQueryParams, UserSummary } from "@/features/users/types";
import { client } from "@/lib/backendApi.ts";
import { useQuery } from "@tanstack/react-query";

export const getUsers = async (params: GetUsersQueryParams): Promise<UserSummary[]> => {
  let url = "users";

  if (Object.keys(params).length !== 0) {
    const query = new URLSearchParams(params);
    url += `?${query.toString()}`;
  }

  return await client.get(url).json();
};

export const useGetUsersQuery = (params: GetUsersQueryParams, options?: { enabled: boolean }) => {
  return useQuery({
    queryFn: async () => {
      return await getUsers(params);
    },
    queryKey: ["users", params],
    enabled: options?.enabled ?? true,
  });
};
