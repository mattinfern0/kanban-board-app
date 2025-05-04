import { client } from "@/lib/backendApi";
import { useQuery } from "@tanstack/react-query";
import { GetTaskQueryParams, TaskDetail } from "../types";

export const getTaskList = async (args: GetTaskQueryParams): Promise<TaskDetail[]> => {
  const url = `tasks?${new URLSearchParams(args).toString()}`;
  return await client.get(url).json();
};

export const useTaskListQuery = (args: GetTaskQueryParams) => {
  return useQuery({
    queryFn: async () => {
      return await getTaskList(args);
    },
    queryKey: ["tasks", args],
  });
};
