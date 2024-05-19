import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CreateTaskBody, TaskDetail } from "../types";

export const createTask = async (body: CreateTaskBody): Promise<TaskDetail> => {
  return await client
    .post(`tasks`, {
      json: body,
    })
    .json();
};

export const useCreateTaskMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (body: CreateTaskBody) => await createTask(body),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
