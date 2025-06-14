import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType, BoardTask } from "../types";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { SortableDraggable } from "@/components/dragging/SortableDraggable.tsx";
import { useDroppable } from "@dnd-kit/core";
import { Badge, Card, Group, Stack, Text } from "@mantine/core";

interface BoardColumnProps {
  boardColumn: BoardColumnType;
  onTaskCardClick: (task: BoardTask) => void;
}

export const BoardColumn = ({ boardColumn, onTaskCardClick }: BoardColumnProps) => {
  const { setNodeRef } = useDroppable({ id: boardColumn.id });

  const cardElements = boardColumn.tasks.map((t) => {
    return (
      <SortableDraggable key={t.id} id={t.id}>
        <BoardTaskCard onClick={onTaskCardClick} boardTask={t} />
      </SortableDraggable>
    );
  });

  return (
    <Stack>
      <Card shadow="md">
        <Group justify="space-between" align="center">
          <Text size="xl">{boardColumn.title}</Text>
          {boardColumn.tasks.length > 0 && (
            <Badge size="xl" color="gray">
              {boardColumn.tasks.length}
            </Badge>
          )}
        </Group>
      </Card>

      <SortableContext id={boardColumn.id} items={boardColumn.tasks} strategy={verticalListSortingStrategy}>
        <Stack
          ref={setNodeRef}
          style={{
            padding: "3px",
            borderRadius: "3px",
          }}
        >
          {cardElements}
        </Stack>
      </SortableContext>
    </Stack>
  );
};
