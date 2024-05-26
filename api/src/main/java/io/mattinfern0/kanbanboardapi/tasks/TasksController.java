package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateUpdateTaskDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.UpdateTaskColumnPositionDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    final TaskService taskService;

    @Autowired
    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("")
    public List<TaskDetailDto> getTaskList() {
        return taskService.getTaskList();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetailDto createTask(@Valid @RequestBody CreateUpdateTaskDto createUpdateTaskDto) {
        return taskService.createTask(createUpdateTaskDto);
    }

    @GetMapping("/{taskId}")
    public TaskDetailDto getTaskDetail(@PathVariable UUID taskId) {
        return taskService.getTaskDetail(taskId);
    }

    @PatchMapping("/{taskId}")
    public TaskDetailDto partialUpdateTask(
        @PathVariable UUID taskId,
        @Valid @RequestBody CreateUpdateTaskDto createUpdateTaskDto
    ) {
        return taskService.updateTask(taskId, createUpdateTaskDto);
    }

    @PutMapping("/{taskId}/assignees")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTaskAssignees(
        @PathVariable UUID taskId,
        @RequestBody @Valid List<@EntityWithIdExists(entityClass = User.class) UUID> assigneeIds
    ) {
        taskService.updateTaskAssignees(taskId, assigneeIds);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
    }

    @PutMapping("/{taskId}/column-position")
    public void updateTaskColumnPosition(
        @PathVariable UUID taskId,
        @Valid @RequestBody UpdateTaskColumnPositionDTO updateTaskColumnPositionDTO
    ) {
        taskService.updateTaskColumnPosition(taskId, updateTaskColumnPositionDTO);
    }
}
