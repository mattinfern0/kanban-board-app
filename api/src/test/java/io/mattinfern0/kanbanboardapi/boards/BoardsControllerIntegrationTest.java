package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardTaskDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import io.mattinfern0.kanbanboardapi.core.entities.*;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.*;
import io.mattinfern0.kanbanboardapi.tasks.TaskStatusService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.util.List;

@SpringBootTest()
@ActiveProfiles(value = "test")
@Testcontainers
@Tag("IntegrationTest")
public class BoardsControllerIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-bullseye");

    final OrganizationRepository organizationRepository;

    final BoardRepository boardRepository;

    final BoardColumnRepository boardColumnRepository;

    final TaskRepository taskRepository;

    final TaskStatusRepository taskStatusRepository;

    final EntityManager entityManager;

    final BoardsController boardsController;

    final TaskStatusService taskStatusService;

    final UserRepository userRepository;

    final static String MOCK_USER_FIREBASE_ID = "e39fARG2F9fqECcIXYjHnzzbZ652";
    @Autowired
    private OrganizationMembershipRepository organizationMembershipRepository;


    @Autowired
    public BoardsControllerIntegrationTest(OrganizationRepository organizationRepository, BoardRepository boardRepository, BoardColumnRepository boardColumnRepository, TaskRepository taskRepository, TaskStatusRepository taskStatusRepository, EntityManager entityManager, BoardsController boardsController, TaskStatusService taskStatusService, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.entityManager = entityManager;
        this.boardsController = boardsController;
        this.taskStatusService = taskStatusService;
        this.userRepository = userRepository;
    }

    @BeforeEach
    @Transactional
    void beforeEach() {
        clearRepositories();
        createDefaultTaskStatuses();
    }

    void clearRepositories() {
        organizationRepository.deleteAll();
        boardRepository.deleteAll();
        boardColumnRepository.deleteAll();
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    void createDefaultTaskStatuses() {
        taskStatusService.findOrCreate(TaskStatusCode.BACKLOG);
        taskStatusService.findOrCreate(TaskStatusCode.TODO);
        taskStatusService.findOrCreate(TaskStatusCode.IN_PROGRESS);
        taskStatusService.findOrCreate(TaskStatusCode.COMPLETED);
        taskStatusService.findOrCreate(TaskStatusCode.OTHER);
    }

    Board createTestBoard() {
        Organization testOrganization = new Organization();
        testOrganization.setDisplayName("Test Organization");

        Board testBoard = new Board();
        testBoard.setOrganization(testOrganization);
        testBoard.setTitle("Test Board");


        BoardColumn testTodoColumn = new BoardColumn();
        testTodoColumn.setBoard(testBoard);
        testTodoColumn.setDisplayOrder(1);
        testTodoColumn.setTitle("Todo");
        testTodoColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.TODO));

        BoardColumn testInProgressColumn = new BoardColumn();
        testInProgressColumn.setBoard(testBoard);
        testInProgressColumn.setDisplayOrder(1);
        testInProgressColumn.setTitle("In Progress");
        testInProgressColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.IN_PROGRESS));

        BoardColumn testCompletedColumn = new BoardColumn();
        testCompletedColumn.setBoard(testBoard);
        testCompletedColumn.setDisplayOrder(1);
        testCompletedColumn.setTitle("Completed");
        testCompletedColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.COMPLETED));

        organizationRepository.save(testOrganization);
        boardRepository.save(testBoard);
        boardColumnRepository.save(testTodoColumn);
        boardColumnRepository.save(testInProgressColumn);
        boardColumnRepository.save(testCompletedColumn);

        return testBoard;
    }

    User createMockUser() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(MOCK_USER_FIREBASE_ID);

        User user = new User();
        user.setFirebaseId(MOCK_USER_FIREBASE_ID);
        user.setFirstName("Test First Name");
        user.setLastName("Test Last Name");

        userRepository.save(user);

        return user;
    }

    Principal createPrincipal(User user) {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(user.getFirebaseId());
        return mockPrincipal;
    }

    void addUserToOrganization(User user, Organization organization) {
        OrganizationMembership membership = new OrganizationMembership(organization, user);
        membership.setRole(OrganizationRole.MEMBER);
        organizationMembershipRepository.save(membership);
    }

    @Nested
    class GetBoardTests {
        @Test
        @Transactional
        public void testGetBoardWorksWithEmptyBoard() {
            Board testBoard = createTestBoard();
            User testUser = createMockUser();
            Principal testPrincipal = createPrincipal(testUser);
            addUserToOrganization(testUser, testBoard.getOrganization());

            BoardDetailDto response = boardsController.getBoard(testPrincipal, testBoard.getId());

            assert response.id().equals(testBoard.getId());
            assert response.title().equals(testBoard.getTitle());

            for (int i = 0; i < testBoard.getBoardColumns().size(); i++) {
                BoardColumn entity = testBoard.getBoardColumns().get(i);
                BoardColumnDto dto = response.boardColumns().get(i);
                assert entity.getId().equals(dto.id());
                assert entity.getTitle().equals(dto.title());
            }
        }

        @Test
        @Transactional
        public void testGetBoardWorksWithBoardWithTasks() {
            Board testBoard = createTestBoard();
            Task testTodoTask1 = new Task();
            testTodoTask1.setTitle("Test Task 1");
            testTodoTask1.setDescription("Testing");
            testTodoTask1.setBoardColumn(testBoard.getBoardColumns().getFirst());
            testTodoTask1.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.TODO));
            testTodoTask1.setOrganization(testBoard.getOrganization());
            taskRepository.save(testTodoTask1);

            Task testTodoTask2 = new Task();
            testTodoTask2.setTitle("Test Task 2");
            testTodoTask2.setDescription("Testing");
            testTodoTask2.setBoardColumn(testBoard.getBoardColumns().getFirst());
            testTodoTask2.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.TODO));
            testTodoTask2.setOrganization(testBoard.getOrganization());
            taskRepository.save(testTodoTask2);

            Task testCompletedTask = new Task();
            testCompletedTask.setTitle("Test Completed 1");
            testCompletedTask.setDescription("A completed task");
            testCompletedTask.setBoardColumn(testBoard.getBoardColumns().get(2));
            testCompletedTask.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.COMPLETED));
            testCompletedTask.setOrganization(testBoard.getOrganization());
            taskRepository.save(testCompletedTask);

            User testUser = createMockUser();
            Principal testPrincipal = createPrincipal(testUser);
            addUserToOrganization(testUser, testBoard.getOrganization());

            BoardDetailDto response = boardsController.getBoard(testPrincipal, testBoard.getId());

            assert response.id().equals(testBoard.getId());
            assert response.title().equals(testBoard.getTitle());

            for (int i = 0; i < testBoard.getBoardColumns().size(); i++) {
                BoardColumn columnEntity = testBoard.getBoardColumns().get(i);
                BoardColumnDto columnDto = response.boardColumns().get(i);
                assert columnEntity.getId().equals(columnDto.id());
                assert columnEntity.getTitle().equals(columnDto.title());

                for (int j = 0; j < columnEntity.getTasks().size(); j++) {
                    Task taskEntity = columnEntity.getTasks().get(j);
                    BoardTaskDto taskDto = columnDto.tasks().get(j);

                    assert taskEntity.getId().equals(taskDto.id());
                    assert taskEntity.getTitle().equals(taskDto.title());
                }
            }
        }
    }

    @Nested
    class DeleteBoardTests {

        @Test
        @Transactional
        public void testDeleteBoard_setsTaskColumnToNullByDefault() {
            Board testBoard = createTestBoard();
            Task testTodoTask1 = new Task();
            testTodoTask1.setTitle("Test Task 1");
            testTodoTask1.setDescription("Testing");
            testTodoTask1.setOrganization(testBoard.getOrganization());
            testBoard.getBoardColumns().getFirst().addTask(testTodoTask1);
            taskRepository.save(testTodoTask1);

            Task testTodoTask2 = new Task();
            testTodoTask2.setTitle("Test Task 2");
            testTodoTask2.setDescription("Testing");
            testTodoTask2.setOrganization(testBoard.getOrganization());
            testBoard.getBoardColumns().getFirst().addTask(testTodoTask2);
            taskRepository.save(testTodoTask2);

            Task testCompletedTask = new Task();
            testCompletedTask.setTitle("Test Completed 1");
            testCompletedTask.setDescription("A completed task");
            testCompletedTask.setOrganization(testBoard.getOrganization());
            testBoard.getBoardColumns().get(2).addTask(testCompletedTask);
            taskRepository.save(testCompletedTask);

            List<Task> boardTasks = taskRepository.findByBoardId(testBoard.getId());

            User testUser = createMockUser();
            Principal testPrincipal = createPrincipal(testUser);
            addUserToOrganization(testUser, testBoard.getOrganization());

            boardsController.deleteBoard(testPrincipal, testBoard.getId(), false);

            for (Task task : boardTasks) {
                entityManager.refresh(task);
                assert task.getBoardColumn() == null;
            }
        }

        @Test
        @Transactional
        public void testDeleteBoard_deletesTasksIfDeleteTasksIsTrue() {
            Board testBoard = createTestBoard();
            Organization testOrganization = testBoard.getOrganization();
            Task testTodoTask1 = new Task();
            testTodoTask1.setTitle("Test Task 1");
            testTodoTask1.setDescription("Testing");
            testTodoTask1.setOrganization(testOrganization);
            testBoard.getBoardColumns().getFirst().addTask(testTodoTask1);
            taskRepository.save(testTodoTask1);

            Task testTodoTask2 = new Task();
            testTodoTask2.setTitle("Test Task 2");
            testTodoTask2.setDescription("Testing");
            testTodoTask2.setOrganization(testOrganization);
            testBoard.getBoardColumns().getFirst().addTask(testTodoTask2);
            taskRepository.save(testTodoTask2);

            Task testCompletedTask = new Task();
            testCompletedTask.setTitle("Test Completed 1");
            testCompletedTask.setDescription("A completed task");
            testCompletedTask.setOrganization(testOrganization);
            testBoard.getBoardColumns().get(2).addTask(testCompletedTask);
            taskRepository.save(testCompletedTask);

            List<Task> boardTasks = taskRepository.findByBoardId(testBoard.getId());

            User testUser = createMockUser();
            Principal testPrincipal = createPrincipal(testUser);
            addUserToOrganization(testUser, testBoard.getOrganization());

            boardsController.deleteBoard(testPrincipal, testBoard.getId(), true);

            for (Task task : boardTasks) {
                assert taskRepository.findById(task.getId()).isEmpty();
            }
        }
    }

    @Test
    @Transactional
    public void test_updateBoardHeader_returnsUpdatedBoard() {
        Board testBoard = createTestBoard();
        UpdateBoardHeaderDTO requestBody = new UpdateBoardHeaderDTO("New Title 23");
        assert !testBoard.getTitle().equals(requestBody.title());

        User testUser = createMockUser();
        Principal testPrincipal = createPrincipal(testUser);
        addUserToOrganization(testUser, testBoard.getOrganization());

        BoardDetailDto response = boardsController.updateBoardHeader(testPrincipal, testBoard.getId(), requestBody);

        assert response.id().equals(testBoard.getId());
        assert response.title().equals(requestBody.title());
    }
}