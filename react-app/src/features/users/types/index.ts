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
