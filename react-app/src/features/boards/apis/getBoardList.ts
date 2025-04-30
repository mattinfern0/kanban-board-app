import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { BoardSummary, GetBoardListQueryParams } from "../types";

export const getBoardList = async (params: GetBoardListQueryParams): Promise<BoardSummary[]> => {
  const url = `boards?${new URLSearchParams(params).toString()}`;
  return await client.get(url).json();
};

export const useBoardListQuery = (params: GetBoardListQueryParams, options: { enabled?: boolean }) => {
  return useQuery({
    queryFn: async () => {
      return await getBoardList(params);
    },
    queryKey: ["boards", params],
    enabled: options.enabled ?? true,
  });
};
