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
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskAssigneeDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskAssigneeDtoMapper;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskDtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    final TaskDtoMapper taskDtoMapper;
    final TaskAssigneeDtoMapper taskAssigneeDtoMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, BoardColumnRepository boardColumnRepository, OrganizationRepository organizationRepository, UserRepository userRepository, TaskStatusService taskStatusService, TaskDtoMapper taskDtoMapper, TaskAssigneeDtoMapper taskAssigneeDtoMapper) {
        this.taskRepository = taskRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.taskStatusService = taskStatusService;
        this.taskDtoMapper = taskDtoMapper;
        this.taskAssigneeDtoMapper = taskAssigneeDtoMapper;
    }

    public List<TaskDetailDto> getTaskList() {
        List<Task> taskEntities = taskRepository.findAll();
        return taskDtoMapper.taskListToTaskDetailDtoList(taskEntities);
    }

    public TaskDetailDto getTaskDetail(UUID taskId) {
        Task entity = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));
        return taskDtoMapper.taskToTaskDetailDto(entity);
    }

    @Transactional
    public TaskDetailDto createTask(@Valid CreateUpdateTaskDto createUpdateTaskDto) {
        Task newTask = taskFromCreateTaskDto(createUpdateTaskDto);
        taskRepository.save(newTask);
        return taskDtoMapper.taskToTaskDetailDto(newTask);
    }

    public TaskDetailDto updateTask(UUID taskId, @Valid CreateUpdateTaskDto createUpdateTaskDto) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException(String.format("Task with id %s not found", taskId));
        }

        Task task = taskFromCreateTaskDto(createUpdateTaskDto);
        task.setId(taskId);
        taskRepository.save(task);
        return taskDtoMapper.taskToTaskDetailDto(task);
    }

    public void deleteTask(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException(String.format("Task with id %s not found", taskId));
        }
        taskRepository.deleteById(taskId);
    }

    public TaskAssigneeDto assignUserToTask(UUID taskId, UUID userId) {
        Task task = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", userId)));

        task.setAssignee(user);

        taskRepository.save(task);
        return taskAssigneeDtoMapper.userToTaskAssigneeDto(user);
    }

    public void removeTaskAssignee(UUID taskId) {
        Task task = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", taskId)));

        task.setAssignee(null);
        taskRepository.save(task);
    }

    Task taskFromCreateTaskDto(CreateUpdateTaskDto createUpdateTaskDtoOld) {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID());
        newTask.setTitle(createUpdateTaskDtoOld.title());
        newTask.setDescription(createUpdateTaskDtoOld.description());

        Organization organization = organizationRepository
            .findById(createUpdateTaskDtoOld.organizationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Organization with id %s not found", createUpdateTaskDtoOld.organizationId())
            ));
        newTask.setOrganization(organization);

        if (createUpdateTaskDtoOld.boardColumnId() != null) {
            BoardColumn boardColumn = boardColumnRepository
                .findById(createUpdateTaskDtoOld.boardColumnId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("BoardColumn with id %s not found", createUpdateTaskDtoOld.boardColumnId())
                ));
            boardColumn.addTask(newTask);
        }

        if (newTask.getTaskStatus() == null) {
            TaskStatusCode statusCode = DEFAULT_TASK_STATUS_CODE;
            if (createUpdateTaskDtoOld.status() != null) {
                statusCode = createUpdateTaskDtoOld.status();
            }

            newTask.setTaskStatus(taskStatusService.findOrCreate(statusCode));
        }

        return newTask;
    }
}
