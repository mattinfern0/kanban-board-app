import { client } from "@/lib/backendApi.ts";
import { BoardTask } from "@/features/boards/types";
import { useQuery } from "@tanstack/react-query";

export const getTaskDetail = async (taskId: string): Promise<BoardTask> => {
  return await client.get(`tasks/${taskId}`).json();
};

export const useTaskDetailQuery = (taskId: string | null) => {
  return useQuery({
    queryFn: async () => {
      return await getTaskDetail(taskId || "");
    },
    queryKey: ["tasks", taskId],
    enabled: taskId != null,
  });
};
