import { DataTable, DataTableColumn, DataTableSortStatus } from "mantine-datatable";
import { useMemo, useState } from "react";
import { sortBy } from "lodash-es";
import { useParams } from "react-router";
import { OrganizationMemberRole } from "@/features/organizations/types";
import { useOrganizationDetailQuery } from "@/features/organizations/apis/getOrganizationDetail.ts";
import { ActionIcon, Group } from "@mantine/core";
import { IconEdit, IconTrash } from "@tabler/icons-react";

interface TableRowValues {
  userId: string;
  firstName: string;
  lastName: string;
  role: OrganizationMemberRole;
}

const ROLE_ENUM_TO_TEXT: Record<OrganizationMemberRole, string> = {
  OWNER: "Owner",
  MEMBER: "Member",
};

const PAGE_SIZES: number[] = [10, 25, 50, 100];

const sortRows = (rows: TableRowValues[], sortStatus: DataTableSortStatus<TableRowValues>): TableRowValues[] => {
  let result = sortBy(rows, [sortStatus.columnAccessor]);
  if (sortStatus.direction === "desc") {
    result = result.reverse();
  }
  return result;
};

export const MemberListTable = (
  props: Readonly<{
    showActions?: boolean;
    onEditClick: (memberId: string) => void;
    onDeleteClick: (memberId: string) => void;
  }>,
) => {
  const { showActions = true, onEditClick, onDeleteClick } = props;
  const { organizationId = "" } = useParams();

  const organizationDetailQuery = useOrganizationDetailQuery(organizationId);
  const [page, setPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(PAGE_SIZES[0]);
  const [sortStatus, setSortStatus] = useState<DataTableSortStatus<TableRowValues>>({
    columnAccessor: "firstName",
    direction: "asc",
  });

  const columns: DataTableColumn<TableRowValues>[] = [
    { accessor: "firstName", title: "First Name", sortable: true, width: "25%" },
    { accessor: "lastName", title: "Last Name", sortable: true, width: "25%" },
    {
      accessor: "status",
      title: "Status",
      sortable: true,
      render: ({ role }) => ROLE_ENUM_TO_TEXT[role as OrganizationMemberRole],
    },
    {
      accessor: "actions",
      hidden: !showActions,
      title: "Actions",
      textAlign: "right",
      render: ({ userId }) => (
        <Group gap={10} wrap="nowrap" justify="flex-end">
          <ActionIcon
            color="secondary"
            variant="subtle"
            size="sm"
            aria-label={`Edit member id ${userId}`}
            onClick={() => onEditClick(userId)}
          >
            <IconEdit />
          </ActionIcon>
          <ActionIcon
            color="danger"
            variant="subtle"
            size="sm"
            aria-label={`Delete member id ${userId}`}
            onClick={() => onDeleteClick(userId)}
          >
            <IconTrash />
          </ActionIcon>
        </Group>
      ),
    },
  ];

  const rowValues: TableRowValues[] = useMemo(() => {
    return (
      organizationDetailQuery.data?.members.map((member) => ({
        userId: member.userId,
        firstName: member.firstName,
        lastName: member.lastName,
        role: member.role,
      })) || []
    );
  }, [organizationDetailQuery.data]);

  const sortedRecords = sortRows(rowValues, sortStatus);

  const from = (page - 1) * pageSize;
  const to = from + pageSize;
  const visibleRecords = sortedRecords.slice(from, to);

  return (
    <DataTable
      height="75vh"
      columns={columns}
      records={visibleRecords}
      striped
      withTableBorder
      verticalSpacing="md"
      totalRecords={rowValues.length}
      page={page}
      recordsPerPage={pageSize}
      recordsPerPageOptions={PAGE_SIZES}
      onPageChange={(newPage) => setPage(newPage)}
      onRecordsPerPageChange={(newPageSize) => setPageSize(newPageSize)}
      sortStatus={sortStatus}
      onSortStatusChange={setSortStatus}
      textSelectionDisabled
    />
  );
};
