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
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
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
    UserAccessService userAccessService;

    @Mock
    TaskStatusService taskStatusService;

    @InjectMocks
    TaskService taskService;

    @Nested
    class CreateTaskTests {

        @Test
        void createsTaskWithCorrectSimpleFields() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.createTask(testPrincipal, testCreateDto);
            assert result.organizationId().equals(testCreateDto.organizationId());
            assert result.title().equals(testCreateDto.title());
            assert result.description().equals(testCreateDto.description());
            assert Objects.equals(result.priority(), testCreateDto.priority());
        }

        @Test
        void createsTaskWithNoColumn_ifBoardColumnIsNull() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.createTask(testPrincipal, testCreateDto);
            assert result.boardColumnId() == null;
        }

        @Test
        void createsTaskWithColumn_ifBoardColumnIsNotNull() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testTaskStatus = new TaskStatus();
            testTaskStatus.setCodename(testStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testTaskStatus);
            testColumn.setBoard(testBoard);

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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.createTask(testPrincipal, testCreateDto);
            assert Objects.equals(result.boardColumnId(), testColumn.getId());
        }

        @Test
        void setsStatusToDefault_whenStatusAndColumnAreNull() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.createTask(testPrincipal, testCreateDto);
            assert result.status().equals(defaultStatusCode);
        }

        @Test
        void setsStatusToColumnStatus_whenStatusAndColumnAreNotNull() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);
            testColumn.setBoard(testBoard);

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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.createTask(testPrincipal, testCreateDto);
            assert result.status().equals(testColumnStatusCode);
        }

        @Test
        void throwsErrorIfOrganizationIdIsPresentAndUserDoesNotHaveAccessToOrganization() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(false);


            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> taskService.createTask(testPrincipal, testCreateDto));
            String expectedMessage = "You don't have permission to create tasks in this organization";
            assert exception.getMessage().equals(expectedMessage);
        }

        @Test
        void throwsErrorIfBoardColumnIdIsPresentAndUserDoesNotHaveAccessToBoard() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());
            testBoard.setOrganization(testOrganization);
            testBoard.setTitle("Test Board");

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);
            testColumn.setBoard(testBoard);

            CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
                testOrganization.getId(),
                "Test Task",
                "Test Description",
                testColumn.getId(),
                testStatusCode,
                null
            );

            Mockito.when(boardColumnRepository.findById(testColumn.getId()))
                .thenReturn(Optional.of(testColumn));

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(false);


            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> taskService.createTask(testPrincipal, testCreateDto));
            String expectedMessage = "You don't have permission to create tasks in this board";
            assert exception.getMessage().equals(expectedMessage);
        }
    }

    @Nested
    class UpdateTaskTests {

        @Test
        void updatesSimpleFields() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, newOrganization.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.updateTask(testPrincipal, existingTask.getId(), updateDTO);
            assert result.organizationId().equals(updateDTO.organizationId());
            assert result.title().equals(updateDTO.title());
            assert result.description().equals(updateDTO.description());
            assert Objects.equals(result.priority(), TaskPriority.LOW);
        }

        @Test
        void removesColumn_ifBoardColumnIsNull() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito.when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testTaskStatus = new TaskStatus();
            testTaskStatus.setCodename(testStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn oldColumn = new BoardColumn();
            oldColumn.setTaskStatus(testTaskStatus);
            oldColumn.setBoard(testBoard);

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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);


            TaskDetailDto result = taskService.updateTask(testPrincipal, existingTask.getId(), testUpdateDto);
            assert result.boardColumnId() == null;
        }

        @Test
        void updatesColumn_ifBoardColumnIsNotNull() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito.when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testTaskStatus = new TaskStatus();
            testTaskStatus.setCodename(testStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testTaskStatus);
            testColumn.setBoard(testBoard);
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, testTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.updateTask(testPrincipal, testTask.getId(), testUpdateDto);
            assert Objects.equals(result.boardColumnId(), testColumn.getId());
        }

        @Test
        void setsStatusToDefault_whenStatusAndColumnAreNull() {
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


            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.updateTask(testPrincipal, existingTask.getId(), testUpdateDto);
            assert result.status().equals(defaultStatusCode);
        }

        @Test
        void setsStatusToColumnStatus_whenStatusAndColumnAreNotNull() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);
            testColumn.setBoard(testBoard);

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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(true);

            TaskDetailDto result = taskService.updateTask(testPrincipal, existingTask.getId(), testUpdateDto);
            assert result.status().equals(testColumnStatusCode);
        }

        @Test
        void throwsErrorIfTaskWithTaskIdDoesNotExist() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);

            assertThrows(ResourceNotFoundException.class, () -> {
                taskService.updateTask(testPrincipal, testTaskId, testUpdateDto);
            });
        }

        @Test
        void throwsErrorPrincipalDoesNotHaveAccessToTask() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito.when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);

            Task existingTask = new Task();
            existingTask.setId(UUID.randomUUID());
            Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

            CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
                testOrganization.getId(),
                "Test Task",
                "Test Description",
                testColumn.getId(),
                testStatusCode,
                null
            );

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(false);

            AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTask(testPrincipal, existingTask.getId(), testCreateDto)
            );
            String expectedMessage = "You do not have permission to update this task";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        void throwsErrorIfOrganizationIdIsPresentAndUserDoesNotHaveAccessToOrganization() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito.when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);

            Task existingTask = new Task();
            existingTask.setId(UUID.randomUUID());
            Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

            CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
                testOrganization.getId(),
                "Test Task",
                "Test Description",
                testColumn.getId(),
                testStatusCode,
                null
            );

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(false);

            AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTask(testPrincipal, existingTask.getId(), testCreateDto)
            );
            String expectedMessage = "You don't have permission to create tasks in this organization";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        void throwsErrorIfBoardColumnIdIsPresentAndUserDoesNotHaveAccessToBoard() {
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito.when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            TaskStatusCode testStatusCode = TaskStatusCode.IN_PROGRESS;

            TaskStatusCode testColumnStatusCode = TaskStatusCode.IN_PROGRESS;
            TaskStatus testColumnTaskStatus = new TaskStatus();
            testColumnTaskStatus.setCodename(testColumnStatusCode);

            Board testBoard = new Board();
            testBoard.setId(UUID.randomUUID());

            BoardColumn testColumn = new BoardColumn();
            testColumn.setId(UUID.randomUUID());
            testColumn.setTaskStatus(testColumnTaskStatus);
            testColumn.setBoard(testBoard);
            Mockito.when(boardColumnRepository.findById(testColumn.getId()))
                .thenReturn(Optional.of(testColumn));

            Task existingTask = new Task();
            existingTask.setId(UUID.randomUUID());
            Mockito.when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

            CreateUpdateTaskDto testCreateDto = new CreateUpdateTaskDto(
                testOrganization.getId(),
                "Test Task",
                "Test Description",
                testColumn.getId(),
                testStatusCode,
                null
            );

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, existingTask.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);
            Mockito
                .when(userAccessService.canAccessBoard(testPrincipal, testBoard.getId()))
                .thenReturn(false);

            AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTask(testPrincipal, existingTask.getId(), testCreateDto)
            );
            String expectedMessage = "You don't have permission to create tasks in this board";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    class DeleteTaskTests {
        @Test
        void throws_error_if_task_with_taskId_does_not_exist() {
            UUID testTaskId = UUID.randomUUID();
            Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(false);

            Principal testPrincipal = Mockito.mock(Principal.class);

            assertThrows(ResourceNotFoundException.class, () -> {
                taskService.deleteTask(testPrincipal, testTaskId);
            });
        }

        @Test
        void throws_error_if_user_does_not_have_access_to_task() {
            UUID testTaskId = UUID.randomUUID();
            Mockito.when(taskRepository.existsById(testTaskId)).thenReturn(true);

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito.when(userAccessService.canAccessTask(testPrincipal, testTaskId)).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> {
                taskService.deleteTask(testPrincipal, testTaskId);
            });
        }
    }

    @Nested
    class UpdateTaskAssigneesTest {
        @Test
        void works_with_validTaskId_and_assigneeIds() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, testTask.getId()))
                .thenReturn(true);

            taskService.updateTaskAssignees(testPrincipal, testTaskId, testAssigneeIds);

            assert testTask.getAssignees().equals(testUsers);
        }

        @Test
        void clears_assignees_when_assigneeIds_is_empty() {
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


            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, testTask.getId()))
                .thenReturn(true);

            taskService.updateTaskAssignees(testPrincipal, testTaskId, new ArrayList<>());

            assert testTask.getAssignees().isEmpty();
        }

        @Test
        void throws_exception_if_at_least_one_of_users_with_assignee_id_not_found() {
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

            Principal testPrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessTask(testPrincipal, testTask.getId()))
                .thenReturn(true);

            assertThrows(ResourceNotFoundException.class, () -> {
                taskService.updateTaskAssignees(testPrincipal, testTaskId, testAssigneeIds);
            });
        }
    }

    @Test
    void throws_error_if_user_does_not_have_access_to_task() {
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

        Principal testPrincipal = Mockito.mock(Principal.class);
        Mockito
            .when(userAccessService.canAccessTask(testPrincipal, testTask.getId()))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            taskService.updateTaskAssignees(testPrincipal, testTaskId, testAssigneeIds);
        });

        String expectedMessage = "You do not have permission to update this task";
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}