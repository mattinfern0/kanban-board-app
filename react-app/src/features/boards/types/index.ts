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

export type Board = {
  title: string;
  boardColumns: BoardColumn[];
};