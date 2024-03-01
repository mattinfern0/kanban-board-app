package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateTaskDto;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import io.mattinfern0.kanbanboardapi.tasks.mappers.TaskDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
    void createTask__worksWithNoColumn() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
        TaskStatus testTaskStatus = new TaskStatus();
        testTaskStatus.setCodename(testStatusCode);

        CreateTaskDto testCreate = new CreateTaskDto();
        testCreate.setOrganizationId(testOrganization.getId());
        testCreate.setTitle("Test Task");
        testCreate.setDescription("Test Description");
        testCreate.setStatus(testStatusCode);
        testCreate.setBoardColumnId(null);

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(
            taskStatusService.findOrCreate(testStatusCode)
        ).thenReturn(testTaskStatus);

        TaskDetailDto result = taskService.createTask(testCreate);
        assert result.getOrganizationId().equals(testCreate.getOrganizationId());
        assert result.getTitle().equals(testCreate.getTitle());
        assert result.getDescription().equals(testCreate.getDescription());
        assert result.getStatus().equals(testCreate.getStatus());
    }

    @Test
    void createTask__setsStatusToDefault_whenStatusAndColumnAreNull() {
        Organization testOrganization = new Organization();
        testOrganization.setId(UUID.randomUUID());

        CreateTaskDto testCreate = new CreateTaskDto();
        testCreate.setOrganizationId(testOrganization.getId());
        testCreate.setTitle("Test Task");
        testCreate.setDescription("Test Description");
        testCreate.setStatus(null);
        testCreate.setBoardColumnId(null);

        TaskStatusCode defaultStatusCode = TaskStatusCode.BACKLOG;
        TaskStatus defaultTaskStatus = new TaskStatus();
        defaultTaskStatus.setCodename(defaultStatusCode);

        Mockito.when(
            organizationRepository.findById(testOrganization.getId())).thenReturn(Optional.of(testOrganization)
        );

        Mockito.when(taskStatusService.findOrCreate(defaultStatusCode)).thenReturn(defaultTaskStatus);


        TaskDetailDto result = taskService.createTask(testCreate);
        assert result.getStatus().equals(defaultStatusCode);
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

        CreateTaskDto testCreate = new CreateTaskDto();
        testCreate.setOrganizationId(testOrganization.getId());
        testCreate.setTitle("Test Task");
        testCreate.setDescription("Test Description");
        testCreate.setStatus(testStatusCode);
        testCreate.setBoardColumnId(testColumn.getId());

        Mockito.when(organizationRepository.findById(testOrganization.getId()))
            .thenReturn(Optional.of(testOrganization));

        Mockito.when(boardColumnRepository.findById(testColumn.getId()))
            .thenReturn(Optional.of(testColumn));

        Mockito.when(taskStatusService.findOrCreate(testColumnStatusCode))
            .thenReturn(testColumnTaskStatus);

        TaskDetailDto result = taskService.createTask(testCreate);
        assert result.getStatus().equals(testColumnStatusCode);
    }
}