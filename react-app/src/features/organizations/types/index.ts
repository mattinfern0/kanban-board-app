import { z } from "zod";

export const OrganizationMemberRoleSchema = z.enum(["OWNER", "MEMBER"]);
export type OrganizationMemberRole = z.infer<typeof OrganizationMemberRoleSchema>;

export const OrganizationMemberSchema = z.object({
  userId: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  role: OrganizationMemberRoleSchema,
});

export type OrganizationMember = z.infer<typeof OrganizationMemberSchema>;

export const OrganizationDetailSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  isPersonal: z.boolean(),
  members: z.array(OrganizationMemberSchema),
});

export type OrganizationDetail = z.infer<typeof OrganizationDetailSchema>;

export const OrganizationInviteeStatusSchema = z.enum(["PENDING", "ACCEPTED", "REVOKED"]);
export type OrganizationInviteeStatus = z.infer<typeof OrganizationInviteeStatusSchema>;

export const OrganizationInviteeSchema = z.object({
  id: z.string(),
  createdAt: z.coerce.date(),
  email: z.string().email(),
  status: OrganizationInviteeStatusSchema,
  expiresAt: z.coerce.date(),
});

export type OrganizationInvitee = z.infer<typeof OrganizationInviteeSchema>;

export type CreateInviteBody = {
  organizationId: string;
  email: string;
};
