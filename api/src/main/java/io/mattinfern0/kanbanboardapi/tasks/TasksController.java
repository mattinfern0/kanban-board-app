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
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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
    public List<TaskDetailDto> getTaskList(Principal principal, UUID organizationId) {
        return taskService.getTaskList(principal, organizationId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetailDto createTask(
        Principal principal,
        @Valid @RequestBody CreateUpdateTaskDto createUpdateTaskDto
    ) {
        return taskService.createTask(principal, createUpdateTaskDto);
    }

    @GetMapping("/{taskId}")
    public TaskDetailDto getTaskDetail(Principal principal, @PathVariable UUID taskId) {
        return taskService.getTaskDetail(principal, taskId);
    }

    @PatchMapping("/{taskId}")
    public TaskDetailDto partialUpdateTask(
        Principal principal,
        @PathVariable UUID taskId,
        @Valid @RequestBody CreateUpdateTaskDto createUpdateTaskDto
    ) {
        return taskService.updateTask(principal, taskId, createUpdateTaskDto);
    }

    @PutMapping("/{taskId}/assignees")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTaskAssignees(
        Principal principal,
        @PathVariable UUID taskId,
        @RequestBody @Valid List<@EntityWithIdExists(entityClass = User.class) UUID> assigneeIds
    ) {
        try {
            taskService.updateTaskAssignees(principal, taskId, assigneeIds);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(Principal principal, @PathVariable UUID taskId) {
        taskService.deleteTask(principal, taskId);
    }

    @PutMapping("/{taskId}/column-position")
    public void updateTaskColumnPosition(
        Principal principal,
        @PathVariable UUID taskId,
        @Valid @RequestBody UpdateTaskColumnPositionDTO updateTaskColumnPositionDTO
    ) {
        taskService.updateTaskColumnPosition(principal, taskId, updateTaskColumnPositionDTO);
    }
}
