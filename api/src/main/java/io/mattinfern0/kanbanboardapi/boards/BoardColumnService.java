package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskOrderItemDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskReorderDto;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BoardColumnService {
    private final BoardColumnRepository boardColumnRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public BoardColumnService(BoardColumnRepository boardColumnRepository, TaskRepository taskRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public List<BoardColumnTaskOrderItemDto> reorderTasks(@Valid BoardColumnTaskReorderDto boardColumnTaskReorderDto) {
        UUID boardColumnId = boardColumnTaskReorderDto.boardColumnId();
        List<BoardColumnTaskOrderItemDto> newOrder = boardColumnTaskReorderDto.newOrder();

        BoardColumn column = boardColumnRepository
            .findById(boardColumnId)
            .orElseThrow(
                () -> new ResourceNotFoundException(String.format("BoardColumn with id %s not found", boardColumnId))
            );

        Map<UUID, Task> taskIdToTask = new HashMap<>();
        column.getTasks().forEach((t) -> {
            taskIdToTask.put(t.getId(), t);
        });

        List<Task> newTaskList = new ArrayList<>();
        for (int i = 0; i < newOrder.size(); i++) {
            UUID taskId = newOrder.get(i).taskId();
            Task task = taskIdToTask.get(taskId);

            if (task == null) {
                continue;
            }

            task.setBoardColumnOrder(i);
            newTaskList.add(task);
        }

        column.setTasks(newTaskList);
        boardColumnRepository.save(column);

        taskRepository.saveAll(newTaskList);
        return newOrder;
    }
}
