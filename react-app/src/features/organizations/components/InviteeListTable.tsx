import { DataTable, DataTableColumn, DataTableSortStatus } from "mantine-datatable";
import { useMemo, useState } from "react";
import { sortBy } from "lodash-es";
import dayjs, { Dayjs } from "dayjs";
import { useParams } from "react-router";
import { useOrganizationInviteesQuery } from "@/features/organizations/apis/getOrganizationInvitees.ts";
import { OrganizationInviteeStatus } from "@/features/organizations/types";
import { ActionIcon } from "@mantine/core";
import { IconTrash } from "@tabler/icons-react";
import { useRevokeInviteMutation } from "@/features/organizations/apis/revokeInvite.ts";
import { enqueueSnackbar } from "notistack";

interface TableRowValues {
  id: string;
  email: string;
  status: OrganizationInviteeStatus;
  createdAt: Dayjs;
  expiresAt: Dayjs;
}

const PAGE_SIZES: number[] = [10, 25, 50, 100];

const sortRows = (rows: TableRowValues[], sortStatus: DataTableSortStatus<TableRowValues>): TableRowValues[] => {
  let result = sortBy(rows, [sortStatus.columnAccessor]);
  if (sortStatus.direction === "desc") {
    result = result.reverse();
  }
  return result;
};

export const InviteeListTable = () => {
  const { organizationId = "" } = useParams();

  const inviteeListQuery = useOrganizationInviteesQuery(organizationId);
  const [page, setPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(PAGE_SIZES[0]);
  const [sortStatus, setSortStatus] = useState<DataTableSortStatus<TableRowValues>>({
    columnAccessor: "createdAt",
    direction: "desc",
  });
  const revokeInviteMutation = useRevokeInviteMutation();

  const columns: DataTableColumn<TableRowValues>[] = [
    { accessor: "email", title: "Email", sortable: true, width: "30%" },
    {
      accessor: "createdAt",
      title: "Created At",
      sortable: true,
      render: ({ createdAt }) => createdAt.format("M-DD-YYYY, h:mm A"),
    },
    {
      accessor: "expiresAt",
      title: "Expires At",
      sortable: true,
      render: ({ expiresAt }) => expiresAt.format("M-DD-YYYY, h:mm A"),
    },
    {
      accessor: "actions",
      title: "Actions",
      textAlign: "right",
      render: ({ id, email }) => (
        <ActionIcon
          color="danger"
          variant="subtle"
          size="sm"
          aria-label={`Revoke invite id ${id}`}
          onClick={() => {
            revokeInviteMutation.mutate(id, {
              onSuccess: () => {
                enqueueSnackbar(`Revoked invite for ${email}`, { variant: "success" });
              },
              onError: (error) => {
                enqueueSnackbar("Failed to revoke invite: " + error.message);
              },
            });
          }}
        >
          <IconTrash />
        </ActionIcon>
      ),
    },
  ];

  const rowValues: TableRowValues[] = useMemo(() => {
    return (
      inviteeListQuery.data?.map((invitee) => ({
        id: invitee.id,
        email: invitee.email,
        status: invitee.status,
        createdAt: dayjs(invitee.createdAt),
        expiresAt: dayjs(invitee.expiresAt),
      })) || []
    );
  }, [inviteeListQuery.data]);

  const sortedRecords = sortRows(rowValues, sortStatus);

  const from = (page - 1) * pageSize;
  const to = from + pageSize;
  const visibleRecords = sortedRecords.slice(from, to);

  return (
    <DataTable
      height="50vh"
      columns={columns}
      records={visibleRecords}
      striped
      withTableBorder
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
