import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { TaskDetail, UpdateTaskColumnPositionBody } from "../types";

export const updateTaskColumnPosition = async (
  taskId: string,
  body: UpdateTaskColumnPositionBody,
): Promise<TaskDetail> => {
  return await client
    .put(`tasks/${taskId}/column-position`, {
      json: body,
    })
    .json();
};

export const useUpdateTaskColumnPositionMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (args: { taskId: string; body: UpdateTaskColumnPositionBody }) =>
      await updateTaskColumnPosition(args.taskId, args.body),
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
