package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.tasks.dtos.*;
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

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
    }

    @PutMapping("/{taskId}/assignee")
    public TaskAssigneeDto assignUserToTask(
        @PathVariable UUID taskId,
        @Valid @RequestBody UpdateTaskAssigneeDto updateTaskAssigneeDto
    ) {
        return taskService.assignUserToTask(taskId, updateTaskAssigneeDto.userId());
    }

    @DeleteMapping("/{taskId}/assignee")
    public void removeTaskAssignee(
        @EntityWithIdExists(entityClass = Task.class) @PathVariable UUID taskId
    ) {
        taskService.removeTaskAssignee(taskId);
    }

    @PutMapping("/{taskId}/column-position")
    public void updateTaskColumnPosition(
        UUID taskId,
        @Valid @RequestBody UpdateTaskColumnPositionDTO updateTaskColumnPositionDTO
    ) {
    }
}
