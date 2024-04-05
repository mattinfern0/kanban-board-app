package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
            testStatusCode
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
            testStatusCode
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
            testStatusCode
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
            testStatusCode
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
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());
        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);
        Mockito.when(taskStatusService.findOrCreate(testStatusCode)).thenReturn(testTaskStatus);

        UUID testTaskId = UUID.randomUUID();
        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode
        );

        TaskDetailDto result = taskService.updateTask(testTaskId, testUpdateDto);
        assert result.organizationId().equals(testUpdateDto.organizationId());
        assert result.title().equals(testUpdateDto.title());
        assert result.description().equals(testUpdateDto.description());
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
        Mockito.when(
            taskStatusService.findOrCreate(testStatusCode)
        ).thenReturn(testTaskStatus);

        UUID testTaskId = UUID.randomUUID();
        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            testStatusCode
        );

        TaskDetailDto result = taskService.updateTask(testTaskId, testUpdateDto);
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

        UUID testTaskId = UUID.randomUUID();
        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode
        );

        TaskDetailDto result = taskService.updateTask(testTaskId, testUpdateDto);
        assert Objects.equals(result.boardColumnId(), testColumn.getId());
    }

    @Test
    void updateTask__setsStatusToDefault_whenStatusAndColumnAreNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        UUID testTaskId = UUID.randomUUID();

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            null,
            null
        );

        TaskStatusCode defaultStatusCode = TaskStatusCode.BACKLOG;
        TaskStatus defaultTaskStatus = new TaskStatus();
        defaultTaskStatus.setCodename(defaultStatusCode);

        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(taskStatusService.findOrCreate(defaultStatusCode)).thenReturn(defaultTaskStatus);


        TaskDetailDto result = taskService.updateTask(testTaskId, testUpdateDto);
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

        UUID testTaskId = UUID.randomUUID();

        CreateUpdateTaskDto testUpdateDto = new CreateUpdateTaskDto(
            testOrganization.getId(),
            "Test Task",
            "Test Description",
            testColumn.getId(),
            testStatusCode
        );

        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        TaskDetailDto result = taskService.updateTask(testTaskId, testUpdateDto);
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
            testStatusCode
        );

        Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(false);

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
}