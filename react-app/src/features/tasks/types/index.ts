import { z } from "zod";
import { TaskStatus } from "@/types";

export const CreateTaskFormSchema = z.object({
  title: z.string({
    required_error: "Title is required.",
  }),
  description: z.string(),
});

export interface CreateTaskFormValues {
  title: string;
  description: string;
  board_id: string;
  column_id: string;
}

export interface UpdateTaskFormValues {
  title: string;
  description: string;
  assignees: string[];
}

export interface CreateTaskBody {
  organizationId: string;

  title: string;
  description: string;

  boardColumnId?: string;
  status?: TaskStatus;
}

export interface UpdateTaskBody {
  organizationId: string;

  title: string;
  description: string;

  boardColumnId: string | null;
  boardColumnOrder: number | null;

  status: TaskStatus | null;
}

export interface UpdateTaskColumnPositionBody {
  boardColumnId: string;
  orderIndex: number;
}

export const TaskAssigneeSummarySchema = z.object({
  userId: z.string(),
  firstName: z.string(),
  lastName: z.string(),
});

export type TaskAssigneeSummary = z.infer<typeof TaskAssigneeSummarySchema>;

export const TaskDetailSchema = z.object({
  id: z.string(),
  organizationId: z.string(),
  title: z.string(),
  description: z.string(),

  boardColumnId: z.string().nullable(),
  boardColumnOrder: z.number().nullable(),
  status: z.nativeEnum(TaskStatus),
  assignees: z.array(TaskAssigneeSummarySchema),
  createdAt: z.coerce.date(),
});

export type TaskDetail = z.infer<typeof TaskDetailSchema>;
