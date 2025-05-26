import { z } from "zod";

export type GetUsersQueryParams = {
  organizationId?: string;
};

export type UserSummary = {
  id: string;
  firstName: string;
  lastName: string;
};

export type UserPrivateDetail = {
  id: string;
  firstName: string;
  lastName: string;
  personalOrganizationId: string;
  organizations: UserDetailOrganizationItem[];
};

export type UserDetailOrganizationItem = {
  id: string;
  displayName: string;
};

export const InviteDetailSchema = z.object({
  id: z.string(),
  organization: z.object({
    id: z.string(),
    name: z.string(),
  }),
  createdAt: z.coerce.date(),
  token: z.string(),
  expiresAt: z.coerce.date(),
  email: z.string(),
});

export type InviteDetail = z.infer<typeof InviteDetailSchema>;
