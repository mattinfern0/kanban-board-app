import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { BoardDetail } from "../types";

export const getBoard = async (boardId: string): Promise<BoardDetail> => {
  return await client.get(`boards/${boardId}`).json();
};

export const useBoardQuery = (boardId: string | null) => {
  return useQuery({
    queryFn: async () => {
      return await getBoard(boardId || "");
    },
    queryKey: ["boards", boardId],
    enabled: boardId != null,
  });
};
