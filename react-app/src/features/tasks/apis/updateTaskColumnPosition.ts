import { client } from "@/lib/backendApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { TaskDetail, UpdateTaskColumnPositionBody } from "../types";
import { Board } from "@/features/boards/types";
import { arrayMove } from "@dnd-kit/sortable";

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
    mutationFn: async (args: { taskId: string; boardId: string; body: UpdateTaskColumnPositionBody }) =>
      await updateTaskColumnPosition(args.taskId, args.body),
    onMutate: async (args) => {
      await queryClient.cancelQueries({
        queryKey: ["boards"],
      });

      const previousBoardData = queryClient.getQueryData(["boards", args.boardId]);
      queryClient.setQueryData(["boards", args.boardId], (oldData: Board) => {
        const newData = structuredClone(oldData);
        const column = newData.boardColumns.find((c) => c.id === args.body.boardColumnId);
        if (!column) {
          return oldData;
        }
        const oldIndex = column.tasks.findIndex((t) => t.id === args.taskId);
        const newIndex = args.body.orderIndex;

        if (oldIndex === -1 || newIndex === -1) {
          return oldData;
        }
        column.tasks = arrayMove(column.tasks, oldIndex, newIndex);

        console.debug("testNewData", newData);

        return newData;
      });

      return { previousBoardData };
    },
    onError: async (_err, args, context) => {
      if (context == null) {
        return;
      }
      queryClient.setQueryData(["boards", args.boardId], context.previousBoardData);
    },
    onSettled: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["boards"],
      });
    },
  });
};
