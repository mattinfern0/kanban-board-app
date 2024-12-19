import { TaskStatus } from "@/types";
import { TaskPriority } from "@/features/tasks/types";

export type BoardTask = {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
  assignees: BoardTaskAssignee[];
  priority: TaskPriority;
};

export type BoardTaskAssignee = {
  userId: string;
  firstName: string;
  lastName: string;
};

export type BoardColumn = {
  id: string;
  title: string;
  taskStatus: TaskStatus;
  tasks: BoardTask[];
};

export type BoardDetail = {
  id: string;
  organizationId: string;
  title: string;
  boardColumns: BoardColumn[];
};

export type BoardSummary = {
  id: string;
  organizationId: string;
  title: string;
};

export type CreateBoardFormValues = {
  title: string;
};

export type CreateBoardBody = {
  organizationId: string;
  title: string;
};

export type UpdateBoardHeaderBody = {
  title: string;
};
