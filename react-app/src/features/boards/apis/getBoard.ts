import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { Board } from "../types";

export const getBoard = async (boardId: string): Promise<Board> => {
  return await client.get(`boards/${boardId}`).json();
};

export const useBoardQuery = (boardId: string) => {
  return useQuery({
    queryFn: async () => {
      return await getBoard(boardId);
    },
    queryKey: ["boards", boardId],
  });
};
