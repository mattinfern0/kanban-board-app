package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateTaskDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskDtoMapper;
import jakarta.persistence.EntityNotFoundException;
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
    final TaskStatusService taskStatusService;

    final TaskDtoMapper taskDtoMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, BoardColumnRepository boardColumnRepository, OrganizationRepository organizationRepository, TaskStatusService taskStatusService, TaskDtoMapper taskDtoMapper) {
        this.taskRepository = taskRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.organizationRepository = organizationRepository;
        this.taskStatusService = taskStatusService;
        this.taskDtoMapper = taskDtoMapper;
    }

    public List<TaskDetailDto> getTaskList() {
        List<Task> taskEntities = taskRepository.findAll();
        return taskDtoMapper.taskListToTaskDetailDtoList(taskEntities);
    }

    public TaskDetailDto getTaskDetail(UUID taskId) {
        Task entity = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Task with id %s not found", taskId)));
        return taskDtoMapper.taskToTaskDetailDto(entity);
    }

    @Transactional
    public TaskDetailDto createTask(@Valid CreateTaskDto createTaskDto) {
        Task newTask = taskFromCreateTaskDto(createTaskDto);
        taskRepository.save(newTask);
        return taskDtoMapper.taskToTaskDetailDto(newTask);
    }

    public void deleteTask(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

    Task taskFromCreateTaskDto(CreateTaskDto createTaskDto) {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID());
        newTask.setTitle(createTaskDto.getTitle());
        newTask.setDescription(createTaskDto.getDescription());

        Organization organization = organizationRepository
            .findById(createTaskDto.getOrganizationId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Organization with id %s not found", createTaskDto.getOrganizationId())
            ));
        newTask.setOrganization(organization);

        BoardColumn boardColumn = null;
        TaskStatus status = null;
        if (createTaskDto.getStatus() != null) {
            status = taskStatusService.findOrCreate(createTaskDto.getStatus());
        }

        if (createTaskDto.getBoardColumnId() != null) {
            boardColumn = boardColumnRepository
                .findById(createTaskDto.getBoardColumnId())
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("BoardColumn with id %s not found", createTaskDto.getBoardColumnId())
                ));
            status = taskStatusService.findOrCreate(boardColumn.getTaskStatus().getCodename());
        }

        if (status == null) {
            status = taskStatusService.findOrCreate(DEFAULT_TASK_STATUS_CODE);
        }

        newTask.setBoardColumn(boardColumn);
        newTask.setTaskStatus(status);
        return newTask;
    }
}
