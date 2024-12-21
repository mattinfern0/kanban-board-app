import { BoardTaskCard } from "@/features/boards/components/BoardTaskCard.tsx";
import { BoardColumn as BoardColumnType, BoardTask } from "../types";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { SortableDraggable } from "@/components/dragging/SortableDraggable.tsx";
import { useDroppable } from "@dnd-kit/core";
import { Card, Group, Stack, Text } from "@mantine/core";

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
    <Card withBorder>
      <Group justify="space-between" mb="1rem" align="center">
        <Text size="xl">{boardColumn.title}</Text>
      </Group>

      <SortableContext id={boardColumn.id} items={boardColumn.tasks} strategy={verticalListSortingStrategy}>
        <Stack
          ref={setNodeRef}
          style={{ height: "60vh", backgroundColor: "lightgray", padding: "3px", overflowY: "scroll" }}
        >
          {cardElements}
        </Stack>
      </SortableContext>
    </Card>
  );
};
