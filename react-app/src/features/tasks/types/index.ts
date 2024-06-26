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

export type TaskAssigneeSummary = {
  userId: string;
  firstName: string;
  lastName: string;
};

export type TaskDetail = {
  id: string;
  organizationId: string;
  title: string;
  description: string;

  boardColumnId: string | null;
  boardColumnOrder: number | null;
  status: TaskStatus;
  assignees: TaskAssigneeSummary[];
};
