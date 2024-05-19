import { TaskStatus } from "@/types";

export type BoardTask = {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
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
