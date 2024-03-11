import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";

export const getBoard = async (boardId: string): Promise<unknown> => {
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
