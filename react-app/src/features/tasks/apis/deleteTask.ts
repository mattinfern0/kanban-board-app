import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const deleteTask = async (taskId: string): Promise<void> => {
  return await client.delete(`tasks/${taskId}`).json();
};

export const useDeleteTaskMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (taskId: string) => await deleteTask(taskId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
