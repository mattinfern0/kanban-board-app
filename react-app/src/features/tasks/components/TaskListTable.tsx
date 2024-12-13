import { useTaskListQuery } from "@/features/tasks/apis/getTaskList.ts";
import { DataTable, DataTableColumn, DataTableSortStatus } from "mantine-datatable";
import { useMemo, useState } from "react";
import { sortBy } from "lodash-es";
import dayjs, { Dayjs } from "dayjs";
import { TaskStatus } from "@/types";

interface TableRowValues {
  id: string;
  title: string;
  status: string;
  createdAt: Dayjs;
}

const STATUS_ENUM_TO_TEXT: Record<TaskStatus, string> = {
  BACKLOG: "Backlog",
  COMPLETED: "Completed",
  IN_PROGRESS: "In Progress",
  TODO: "Todo",
  OTHER: "Other",
};

const columns: DataTableColumn<TableRowValues>[] = [
  { accessor: "title", title: "Title", sortable: true, width: "50%" },
  { accessor: "status", title: "Status", render: ({ status }) => STATUS_ENUM_TO_TEXT[status as TaskStatus] },
  {
    accessor: "createdAt",
    title: "Created At",
    sortable: true,
    render: ({ createdAt }) => createdAt.format("M-DD-YYYY, h:mm A"),
  },
];

const PAGE_SIZES: number[] = [10, 25, 50, 100];

const sortRows = (rows: TableRowValues[], sortStatus: DataTableSortStatus<TableRowValues>): TableRowValues[] => {
  let result = sortBy(rows, [sortStatus.columnAccessor]);
  if (sortStatus.direction === "desc") {
    result = result.reverse();
  }
  return result;
};

export const TaskListTable = () => {
  const taskListQuery = useTaskListQuery();
  const [page, setPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(PAGE_SIZES[0]);
  const [sortStatus, setSortStatus] = useState<DataTableSortStatus<TableRowValues>>({
    columnAccessor: "createdAt",
    direction: "desc",
  });

  const rowValues: TableRowValues[] = useMemo(() => {
    return (
      taskListQuery.data?.map((task) => ({
        id: task.id,
        title: task.title,
        status: task.status,
        createdAt: dayjs(task.createdAt),
      })) || []
    );
  }, [taskListQuery.data]);

  const sortedRecords = sortRows(rowValues, sortStatus);

  const from = (page - 1) * pageSize;
  const to = from + pageSize;
  const visibleRecords = sortedRecords.slice(from, to);

  return (
    <DataTable
      height={500}
      columns={columns}
      records={visibleRecords}
      striped
      withTableBorder
      withColumnBorders
      totalRecords={rowValues.length}
      page={page}
      recordsPerPage={pageSize}
      recordsPerPageOptions={PAGE_SIZES}
      onPageChange={(newPage) => setPage(newPage)}
      onRecordsPerPageChange={(newPageSize) => setPageSize(newPageSize)}
      sortStatus={sortStatus}
      onSortStatusChange={setSortStatus}
    />
  );
};
