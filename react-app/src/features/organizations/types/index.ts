import { z } from "zod";

export const OrganizationMemberSchema = z.object({
  userId: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  role: z.enum(["OWNER", "MEMBER"]),
});

export type OrganizationMember = z.infer<typeof OrganizationMemberSchema>;

export const OrganizationDetailSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  isPersonal: z.boolean(),
  members: z.array(OrganizationMemberSchema),
});

export type OrganizationDetail = z.infer<typeof OrganizationDetailSchema>;
