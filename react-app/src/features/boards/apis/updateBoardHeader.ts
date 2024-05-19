import { client } from "@/lib/backendApi.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { BoardDetail, UpdateBoardHeaderBody } from "@/features/boards/types";

export const updateBoardHeader = async (taskId: string, body: UpdateBoardHeaderBody): Promise<BoardDetail> => {
  return await client
    .put(`boards/${taskId}/header`, {
      json: body,
    })
    .json();
};

export const useUpdateBoardHeaderMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (args: { boardId: string; body: UpdateBoardHeaderBody }) =>
      await updateBoardHeader(args.boardId, args.body),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
