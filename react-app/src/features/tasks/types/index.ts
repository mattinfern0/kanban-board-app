import { z } from "zod";

export const CreateTaskFormSchema = z.object({
  title: z.string({
    required_error: "Title is required.",
  }),
  description: z.string(),
});

export interface CreateTaskFormValues {
  title: string;
  description: string;
  column_id: string;
}
