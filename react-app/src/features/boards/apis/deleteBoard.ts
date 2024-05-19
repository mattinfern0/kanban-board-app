import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { BoardDetail } from "../types";

export const deleteBoard = async (boardId: string, deleteTasks: boolean): Promise<BoardDetail> => {
  let url = `boards/${boardId}`;

  if (deleteTasks) {
    const queryParams = new URLSearchParams();
    queryParams.append("deleteTasks", "true");
    url += `?${queryParams.toString()}`;
  }
  return await client.delete(url).json();
};

export const useDeleteBoardMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (args: { boardId: string; deleteTasks: boolean }) =>
      await deleteBoard(args.boardId, args.deleteTasks),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
