import { useTaskListQuery } from "@/features/tasks/apis/getTaskList.ts";
import { DataTable, DataTableColumn, DataTableSortStatus } from "mantine-datatable";
import { useMemo, useState } from "react";
import { sortBy } from "lodash-es";
import dayjs, { Dayjs } from "dayjs";
import { TaskStatus } from "@/types";
import { TaskPriority } from "@/features/tasks/types";
import { Group } from "@mantine/core";
import { TaskPriorityIcon } from "@/features/tasks/components/TaskPriorityIcon.tsx";

interface TableRowValues {
  id: string;
  title: string;
  priority: TaskPriority | null;
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

const PRIORITY_ENUM_TO_TEXT: Record<TaskPriority, string> = {
  LOW: "Low",
  MEDIUM: "Medium",
  HIGH: "High",
};

const columns: DataTableColumn<TableRowValues>[] = [
  { accessor: "title", title: "Title", sortable: true, width: "50%" },
  {
    accessor: "priority",
    title: "Priority",
    sortable: true,
    render: ({ priority }) => {
      if (!priority) {
        return "-";
      }

      return (
        <Group align="center">
          <TaskPriorityIcon priority={priority} />
          {PRIORITY_ENUM_TO_TEXT[priority] || priority}
        </Group>
      );
    },
  },
  {
    accessor: "status",
    title: "Status",
    sortable: true,
    render: ({ status }) => STATUS_ENUM_TO_TEXT[status as TaskStatus],
  },
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

interface Props {
  onRowClick: (taskId: string) => void;
}

export const TaskListTable = (props: Readonly<Props>) => {
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
        priority: task.priority,
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
      height="75vh"
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
      textSelectionDisabled
      onRowClick={({ record }) => props.onRowClick(record.id)}
    />
  );
};
