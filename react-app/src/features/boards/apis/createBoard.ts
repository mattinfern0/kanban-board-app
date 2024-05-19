import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { BoardDetail, CreateBoardBody } from "../types";

export const createBoard = async (body: CreateBoardBody): Promise<BoardDetail> => {
  return await client
    .post(`boards`, {
      json: body,
    })
    .json();
};

export const useCreateBoardMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (body: CreateBoardBody) => await createBoard(body),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
