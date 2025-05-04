package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateUpdateTaskDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.UpdateTaskColumnPositionDTO;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskDtoMapper;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    static final TaskStatusCode DEFAULT_TASK_STATUS_CODE = TaskStatusCode.BACKLOG;

    final TaskRepository taskRepository;
    final BoardColumnRepository boardColumnRepository;
    final OrganizationRepository organizationRepository;
    final UserRepository userRepository;
    final TaskStatusService taskStatusService;
    final UserAccessService userAccessService;

    final TaskDtoMapper taskDtoMapper;


    @Autowired
    public TaskService(
        TaskRepository taskRepository,
        BoardColumnRepository boardColumnRepository,
        OrganizationRepository organizationRepository,
        UserRepository userRepository,
        TaskStatusService taskStatusService,
        TaskDtoMapper taskDtoMapper,
        UserAccessService userAccessService) {
        this.taskRepository = taskRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.taskStatusService = taskStatusService;
        this.taskDtoMapper = taskDtoMapper;
        this.userAccessService = userAccessService;
    }

    public List<TaskDetailDto> getTaskList(Principal principal, @Nullable UUID organizationId) {
        if (organizationId == null) {
            return List.of();
        }

        if (!userAccessService.canAccessOrganization(principal, organizationId)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        List<Task> taskEntities = taskRepository.findByOrganizationId(organizationId);
        return taskDtoMapper.taskListToTaskDetailDtoList(taskEntities);
    }

    public TaskDetailDto getTaskDetail(Principal principal, UUID taskId) {
        Task entity = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));
        return taskDtoMapper.taskToTaskDetailDto(entity);
    }

    @Transactional
    public TaskDetailDto createTask(Principal principal, @Valid CreateUpdateTaskDto createUpdateTaskDto) {
        authorizeCreateUpdateTask(principal, createUpdateTaskDto);

        Task newTask = taskFromCreateTaskDto(createUpdateTaskDto);
        taskRepository.save(newTask);
        return taskDtoMapper.taskToTaskDetailDto(newTask);
    }

    void authorizeCreateUpdateTask(Principal principal, CreateUpdateTaskDto createUpdateTaskDto) {
        if (
            createUpdateTaskDto.organizationId() != null
                && !userAccessService.canAccessOrganization(principal, createUpdateTaskDto.organizationId())
        ) {
            throw new AccessDeniedException("You don't have permission to create tasks in this organization");
        }

        if (
            createUpdateTaskDto.boardColumnId() != null
        ) {
            BoardColumn column = boardColumnRepository
                .findById(createUpdateTaskDto.boardColumnId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("BoardColumn with id %s not found", createUpdateTaskDto.boardColumnId())
                ));

            if (!userAccessService.canAccessBoard(principal, column.getBoard().getId())) {
                throw new AccessDeniedException("You don't have permission to create tasks in this board");
            }
        }
    }

    @Transactional
    public TaskDetailDto updateTask(Principal principal, UUID taskId, @Valid CreateUpdateTaskDto dto) {
        Task task = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));

        Organization targetOrganization = organizationRepository
            .findById(dto.organizationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Organization with id %s not found", dto.organizationId())
            ));

        if (!userAccessService.canAccessTask(principal, taskId)) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        authorizeCreateUpdateTask(principal, dto);

        task.setDescription(dto.description());
        task.setTitle(dto.title());
        task.setOrganization(targetOrganization);
        task.setPriority(dto.priority());

        if (dto.boardColumnId() == null && task.getBoardColumn() != null) {
            task.getBoardColumn().removeTask(task);
        } else if (dto.boardColumnId() != null) {
            BoardColumn newColumn = boardColumnRepository
                .findById(dto.boardColumnId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("BoardColumn with id %s not found", dto.boardColumnId())
                ));

            BoardColumn oldColumn = task.getBoardColumn();


            if (oldColumn != null && !oldColumn.getId().equals(newColumn.getId())) {
                oldColumn.removeTask(task);
                taskRepository.saveAllAndFlush(oldColumn.getTasks());
                newColumn.addTask(task);
            } else if (oldColumn == null) {
                newColumn.addTask(task);
            }
        }

        if (dto.status() != null && dto.boardColumnId() == null) {
            task.setTaskStatus(taskStatusService.findOrCreate(dto.status()));
        } else if (dto.status() == null && dto.boardColumnId() == null) {
            task.setTaskStatus(taskStatusService.findOrCreate(DEFAULT_TASK_STATUS_CODE));
        }

        taskRepository.saveAndFlush(task);
        return taskDtoMapper.taskToTaskDetailDto(task);
    }

    @Transactional
    public void updateTaskAssignees(Principal principal, UUID taskId, List<UUID> assigneeIds) {
        Task task = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));

        if (!userAccessService.canAccessTask(principal, taskId)) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        if (assigneeIds.isEmpty()) {
            task.getAssignees().clear();
        } else {
            List<User> assignees = userRepository.findAllById(assigneeIds);

            if (assignees.size() != assigneeIds.size()) {
                throw new ResourceNotFoundException("One or more assignees not found");
            }

            List<User> unauthorizedAssignees = assignees.stream()
                .filter(user -> !userAccessService.canAccessOrganization(user, task.getOrganization().getId()))
                .toList();
            if (!unauthorizedAssignees.isEmpty()) {
                throw new IllegalArgumentException("Some of the assignees do not belong to the task's organization");
            }

            task.setAssignees(assignees);
        }
        taskRepository.saveAndFlush(task);
    }

    public void deleteTask(Principal principal, UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException(String.format("Task with id %s not found", taskId));
        }

        if (!userAccessService.canAccessTask(principal, taskId)) {
            throw new AccessDeniedException("You do not have permission to delete this task");
        }

        taskRepository.deleteById(taskId);
    }


    @Transactional
    public void updateTaskColumnPosition(Principal principal, UUID taskId, UpdateTaskColumnPositionDTO dto) {
        Task task = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));

        BoardColumn newColumn = boardColumnRepository
            .findById(dto.boardColumnId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("BoardColumn with id %s not found", dto.boardColumnId())
            ));

        BoardColumn oldColumn = task.getBoardColumn();

        if (oldColumn != null && !oldColumn.getId().equals(newColumn.getId())) {
            task.getBoardColumn().removeTask(task);
            taskRepository.saveAllAndFlush(oldColumn.getTasks());
        }

        newColumn.insertTask(task, dto.orderIndex());

        taskRepository.saveAllAndFlush(newColumn.getTasks());
    }

    Task taskFromCreateTaskDto(CreateUpdateTaskDto dto) {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID());
        newTask.setTitle(dto.title());
        newTask.setDescription(dto.description());
        newTask.setPriority(dto.priority());

        Organization organization = organizationRepository
            .findById(dto.organizationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Organization with id %s not found", dto.organizationId())
            ));
        newTask.setOrganization(organization);

        if (dto.boardColumnId() != null) {
            BoardColumn boardColumn = boardColumnRepository
                .findById(dto.boardColumnId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("BoardColumn with id %s not found", dto.boardColumnId())
                ));
            boardColumn.addTask(newTask);
        }

        if (newTask.getTaskStatus() == null) {
            TaskStatusCode statusCode = DEFAULT_TASK_STATUS_CODE;
            if (dto.status() != null) {
                statusCode = dto.status();
            }

            newTask.setTaskStatus(taskStatusService.findOrCreate(statusCode));
        }

        return newTask;
    }
}
