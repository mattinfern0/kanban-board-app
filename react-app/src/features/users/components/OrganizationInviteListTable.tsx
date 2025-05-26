import { DataTable, DataTableColumn, DataTableSortStatus } from "mantine-datatable";
import { useMemo, useState } from "react";
import { sortBy } from "lodash-es";
import dayjs, { Dayjs } from "dayjs";
import { ActionIcon, Group } from "@mantine/core";
import { IconCheck, IconX } from "@tabler/icons-react";
import { useRevokeInviteMutation } from "@/features/organizations/apis/revokeInvite.ts";
import { enqueueSnackbar } from "notistack";
import { useCurrentUserInvitesQuery } from "@/features/users/apis/getCurrentUserInvites.ts";
import { InviteDetail } from "@/features/users/types";
import { useAcceptInviteMutation } from "@/features/organizations/apis/acceptInvite.ts";

interface TableRowValues {
  id: string;
  organizationName: string;
  createdAt: Dayjs;
}

const PAGE_SIZES: number[] = [10, 25, 50, 100];

const sortRows = (rows: TableRowValues[], sortStatus: DataTableSortStatus<TableRowValues>): TableRowValues[] => {
  let result = sortBy(rows, [sortStatus.columnAccessor]);
  if (sortStatus.direction === "desc") {
    result = result.reverse();
  }
  return result;
};

export const OrganizationInviteListTable = () => {
  const currentUserInvitesQuery = useCurrentUserInvitesQuery();
  const [page, setPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(PAGE_SIZES[0]);
  const [sortStatus, setSortStatus] = useState<DataTableSortStatus<TableRowValues>>({
    columnAccessor: "createdAt",
    direction: "desc",
  });
  const revokeInviteMutation = useRevokeInviteMutation();
  const acceptInviteMutation = useAcceptInviteMutation();

  const idToInvite: Record<string, InviteDetail> = {};
  currentUserInvitesQuery.data?.forEach((invite) => {
    idToInvite[invite.id] = invite;
  });

  const columns: DataTableColumn<TableRowValues>[] = [
    { accessor: "organizationName", title: "Organization", sortable: true, width: "50%" },
    {
      accessor: "createdAt",
      title: "Invited At",
      sortable: true,
      render: ({ createdAt }) => createdAt.format("M-DD-YYYY, h:mm A"),
    },
    {
      accessor: "actions",
      title: "Actions",
      textAlign: "right",
      render: ({ id }) => (
        <Group justify="right" wrap="nowrap">
          <ActionIcon
            color="primary"
            size="sm"
            aria-label={`Accept invite id ${id}`}
            onClick={() => {
              const invite = idToInvite[id];
              if (!invite || acceptInviteMutation.isPending) {
                return;
              }

              acceptInviteMutation.mutate(invite.token, {
                onSuccess: () => {
                  enqueueSnackbar(`Joined ${invite.organization.name}!`, { variant: "success" });
                },
                onError: (error) => {
                  enqueueSnackbar("Failed to accept invite: " + error.message);
                },
              });
            }}
          >
            <IconCheck />
          </ActionIcon>
          <ActionIcon
            color="danger"
            size="sm"
            aria-label={`Reject invite id ${id}`}
            onClick={() => {
              if (revokeInviteMutation.isPending) {
                return;
              }

              revokeInviteMutation.mutate(id, {
                onSuccess: () => {
                  enqueueSnackbar(`Rejected invite`, { variant: "success" });
                },
                onError: (error) => {
                  enqueueSnackbar("Failed to reject invite: " + error.message);
                },
              });
            }}
          >
            <IconX />
          </ActionIcon>
        </Group>
      ),
    },
  ];

  const rowValues: TableRowValues[] = useMemo(() => {
    return (
      currentUserInvitesQuery.data?.map((invite) => ({
        id: invite.id,
        organizationName: invite.organization.name,
        createdAt: dayjs(invite.createdAt),
      })) || []
    );
  }, [currentUserInvitesQuery.data]);

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
      noRecordsText="No Pending Invites"
    />
  );
};
