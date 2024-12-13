import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { TaskDetail } from "../types";

export const getTaskList = async (): Promise<TaskDetail[]> => {
  return await client.get(`tasks`).json();
};

export const useTaskListQuery = () => {
  return useQuery({
    queryFn: async () => {
      return await getTaskList();
    },
    queryKey: ["tasks"],
  });
};
