import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { TaskDetail } from "../types";

export const updateTaskAssignees = async (taskId: string, assignees: string[]): Promise<TaskDetail> => {
  return await client
    .put(`tasks/${taskId}/assignees`, {
      json: assignees,
    })
    .json();
};

export const useUpdateTaskAssigneesMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (args: { taskId: string; assigneeIds: string[] }) =>
      await updateTaskAssignees(args.taskId, args.assigneeIds),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
