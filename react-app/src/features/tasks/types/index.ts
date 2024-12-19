import { z } from "zod";
import { TaskStatus } from "@/types";

const TaskPrioritySchema = z.enum(["LOW", "MEDIUM", "HIGH"]);
export type TaskPriority = z.infer<typeof TaskPrioritySchema>;

export const CreateTaskFormSchema = z.object({
  title: z.string({
    required_error: "Title is required.",
  }),
  description: z.string(),
  board_id: z.string(),
  column_id: z.string(),
  priority: z.nullable(TaskPrioritySchema),
});

export type CreateTaskFormValues = z.infer<typeof CreateTaskFormSchema>;

export interface UpdateTaskFormValues {
  title: string;
  description: string;
  priority: TaskPriority | null;
  assignees: string[];
}

export interface CreateTaskBody {
  organizationId: string;

  title: string;
  description: string;

  boardColumnId?: string;
  status?: TaskStatus;
  priority: TaskPriority | null;
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
