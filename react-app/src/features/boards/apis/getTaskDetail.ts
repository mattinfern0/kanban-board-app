import { client } from "@/lib/backendApi.ts";
import { useQuery } from "@tanstack/react-query";
import { TaskDetail } from "@/features/tasks/types";

export const getTaskDetail = async (taskId: string): Promise<TaskDetail> => {
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
