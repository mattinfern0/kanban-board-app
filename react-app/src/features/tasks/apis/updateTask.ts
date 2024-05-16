import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { TaskDetail, UpdateTaskBody } from "../types";

export const updateTask = async (taskId: string, body: UpdateTaskBody): Promise<TaskDetail> => {
  return await client
    .patch(`tasks/${taskId}`, {
      json: body,
    })
    .json();
};

export const useUpdateTaskMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (args: { taskId: string; body: UpdateTaskBody }) => await updateTask(args.taskId, args.body),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
