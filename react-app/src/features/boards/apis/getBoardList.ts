import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { BoardSummary } from "../types";

export const getBoardList = async (): Promise<BoardSummary[]> => {
  return await client.get(`boards`).json();
};

export const useBoardListQuery = () => {
  return useQuery({
    queryFn: async () => {
      return await getBoardList();
    },
    queryKey: ["boards"],
  });
};
