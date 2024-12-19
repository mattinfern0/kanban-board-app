package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.*;
import io.mattinfern0.kanbanboardapi.core.enums.TaskPriority;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateUpdateTaskDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskDtoMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Spy
    TaskDtoMapper taskDtoMapper = Mappers.getMapper(TaskDtoMapper.class);

    @Mock
    OrganizationRepository organizationRepository;

    @Mock
    BoardColumnRepository boardColumnRepository;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TaskStatusService taskStatusService;

    @InjectMocks
    TaskService taskService;

    @Test
    void createTask__createsTaskWithCorrectSimpleFields() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode,
            TaskPriority.HIGH
        );

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(
            taskStatusService.findOrCreate(testStatusCode)
        ).thenReturn(testTaskStatus);

        TaskDetailDto result = taskService.createTask(testCreateDto);
        assert result.organizationId().equals(testCreateDto.organizationId());
        assert result.title().equals(testCreateDto.title());
        assert result.description().equals(testCreateDto.description());
        assert Objects.equals(result.priority(), testCreateDto.priority());
    }

    @Test
    void createTask__createsTaskWithNoColumn_ifBoardColumnIsNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode,
            null
        );

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(
            taskStatusService.findOrCreate(testStatusCode)
        ).thenReturn(testTaskStatus);

        TaskDetailDto result = taskService.createTask(testCreateDto);
        assert result.boardColumnId() == null;
    }

    @Test
    void createTask__createsTaskWithColumn_ifBoardColumnIsNotNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        BoardColumn testColumn = new BoardColumn();
        testColumn.setId(UUID.randomUUID());
        testColumn.setTaskStatus(testTaskStatus);

        CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode,
            null
        );

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        TaskDetailDto result = taskService.createTask(testCreateDto);
        assert Objects.equals(result.boardColumnId(), testColumn.getId());
    }

    @Test
    void createTask__setsStatusToDefault_whenStatusAndColumnAreNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            null,
            null
        );

        TaskStatusCode defaultStatusCode = TaskStatusCode.BACKLOG;
        TaskStatus defaultTaskStatus = new TaskStatus();
        defaultTaskStatus.setCodename(defaultStatusCode);

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(taskStatusService.findOrCreate(defaultStatusCode)).thenReturn(defaultTaskStatus);


        TaskDetailDto result = taskService.createTask(testCreateDto);
        assert result.status().equals(defaultStatusCode);
    }

    @Test
    void createTask__setsStatusToColumnStatus_whenStatusAndColumnAreNotNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

        TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testColumnTaskStatus = new TaskStatus();
        testColumnTaskStatus.setCodename(testColumnStatusCode);

        BoardColumn testColumn = new BoardColumn();
        testColumn.setId(UUID.randomUUID());
        testColumn.setTaskStatus(testColumnTaskStatus);

        CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode,
            null
        );

        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        TaskDetailDto result = taskService.createTask(testCreateDto);
        assert result.status().equals(testColumnStatusCode);
    }

    @Test
    void updateTask__updatesSimpleFields() {
        Organization newOrganization = new Organization();
        newOrganization.setId(UUID.randomUUID());
        Mockito.when(organizationRepository.findById(newOrganization.getId()))
            .thenReturn(Optional.of(newOrganization));

        TaskStatusCode newStatusCode = TaskStatusCode.IN_PROGRESS;

        Organization oldOrganization = new Organization();
        oldOrganization.setId(UUID.randomUUID());

        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        existingTask.setOrganization(oldOrganization);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");

        Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));


        CreateUpdateTaskDto updateDTO = new CreateUpdateTaskDto(
            newOrganization.getId(),
            "New Title",
            "New Description",
            null,
            newStatusCode,
            TaskPriority.LOW
        );

        TaskDetailDto result = taskService.updateTask(existingTask.getId(), updateDTO);
        assert result.organizationId().equals(updateDTO.organizationId());
        assert result.title().equals(updateDTO.title());
        assert result.description().equals(updateDTO.description());
        assert Objects.equals(result.priority(), TaskPriority.LOW);
    }

    @Test
    void updateTask__removesColumn_ifBoardColumnIsNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());
        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        BoardColumn oldColumn = new BoardColumn();
        oldColumn.setTaskStatus(testTaskStatus);

        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        existingTask.setBoardColumn(oldColumn);
        Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode,
            null
        );

        TaskDetailDto result = taskService.updateTask(existingTask.getId(), testUpdateDto);
        assert result.boardColumnId() == null;
    }

    @Test
    void updateTask__updatesColumn_ifBoardColumnIsNotNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());
        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        BoardColumn testColumn = new BoardColumn();
        testColumn.setId(UUID.randomUUID());
        testColumn.setTaskStatus(testTaskStatus);
        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        Task testTask = new Task();
        testTask.setId(UUID.randomUUID());
        Mockito.when(taskRepository.findById(testTask.getId())).thenReturn(Optional.of(testTask));

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode,
            null
        );

        TaskDetailDto result = taskService.updateTask(testTask.getId(), testUpdateDto);
        assert Objects.equals(result.boardColumnId(), testColumn.getId());
    }

    @Test
    void updateTask__setsStatusToDefault_whenStatusAndColumnAreNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            null,
            null
        );

        TaskStatusCode defaultStatusCode = TaskStatusCode.BACKLOG;
        TaskStatus defaultTaskStatus = new TaskStatus();
        defaultTaskStatus.setCodename(defaultStatusCode);

        Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(taskStatusService.findOrCreate(defaultStatusCode)).thenReturn(defaultTaskStatus);


        TaskDetailDto result = taskService.updateTask(existingTask.getId(), testUpdateDto);
        assert result.status().equals(defaultStatusCode);
    }

    @Test
    void updateTask__setsStatusToColumnStatus_whenStatusAndColumnAreNotNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

        TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testColumnTaskStatus = new TaskStatus();
        testColumnTaskStatus.setCodename(testColumnStatusCode);

        BoardColumn testColumn = new BoardColumn();
        testColumn.setId(UUID.randomUUID());
        testColumn.setTaskStatus(testColumnTaskStatus);

        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode,
            null
        );

        Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        TaskDetailDto result = taskService.updateTask(existingTask.getId(), testUpdateDto);
        assert result.status().equals(testColumnStatusCode);
    }

    @Test
    void updateTask__throwsErrorIfTaskWithTaskIdDoesNotExist() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

        UUID testTaskId = UUID.randomUUID();

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode,
            null
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(testTaskId, testUpdateDto);
        });
    }

    @Test
    void deleteTask__throwsErrorIfTaskWithTaskIdDoesNotExist() {
        UUID testTaskId = UUID.randomUUID();
        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(testTaskId);
        });
    }

    @Test
    void updateTaskAssignees_works_with_validTaskId_and_assigneeIds() {
        UUID testTaskId = UUID.randomUUID();
        List<UUID> testAssigneeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<User> testUsers = new ArrayList<>();
        for (UUID assigneeId : testAssigneeIds) {
            User user = new User();
            user.setId(assigneeId);
            testUsers.add(user);
        }
        Task testTask = new Task();
        testTask.setId(testTaskId);
        Mockito.when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        Mockito.when(userRepository.findAllById(testAssigneeIds)).thenReturn(testUsers);
        taskService.updateTaskAssignees(testTaskId, testAssigneeIds);

        assert testTask.getAssignees().equals(testUsers);
    }

    @Test
    void updateTaskAssignees_clears_assignees_when_assigneeIds_is_empty() {
        UUID testTaskId = UUID.randomUUID();
        List<UUID> testAssigneeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<User> testUsers = new ArrayList<>();
        for (UUID assigneeId : testAssigneeIds) {
            User user = new User();
            user.setId(assigneeId);
            testUsers.add(user);
        }
        Task testTask = new Task();
        testTask.setId(testTaskId);
        testTask.setAssignees(testUsers);
        Mockito.when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        taskService.updateTaskAssignees(testTaskId, new ArrayList<>());

        assert testTask.getAssignees().isEmpty();
    }

    @Test
    void updateTaskAssignees_throws_exception_if_at_least_one_of_users_with_assignee_id_not_found() {
        UUID testTaskId = UUID.randomUUID();
        List<UUID> testAssigneeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<User> testUsers = new ArrayList<>();
        User testUser = new User();
        testUser.setId(testAssigneeIds.getFirst());
        testUsers.add(testUser);

        Task testTask = new Task();
        testTask.setId(testTaskId);
        Mockito.when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        Mockito.when(userRepository.findAllById(testAssigneeIds)).thenReturn(testUsers);

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTaskAssignees(testTaskId, testAssigneeIds);
        });
    }
}