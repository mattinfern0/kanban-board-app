package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardTaskDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.*;
import io.mattinfern0.kanbanboardapi.tasks.TaskStatusService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

@SpringBootTest()
@Tag("IntegrationTest")
public class BoardsControllerIntegrationTest {
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-bullseye");

    final OrganizationRepository organizationRepository;

    final BoardRepository boardRepository;

    final BoardColumnRepository boardColumnRepository;

    final TaskRepository taskRepository;

    final TaskStatusRepository taskStatusRepository;

    final EntityManager entityManager;

    final BoardsController boardsController;

    final TaskStatusService taskStatusService;

    @Autowired
    public BoardsControllerIntegrationTest(OrganizationRepository organizationRepository, BoardRepository boardRepository, BoardColumnRepository boardColumnRepository, TaskRepository taskRepository, TaskStatusRepository taskStatusRepository, EntityManager entityManager, BoardsController boardsController, TaskStatusService taskStatusService) {
        this.organizationRepository = organizationRepository;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.entityManager = entityManager;
        this.boardsController = boardsController;
        this.taskStatusService = taskStatusService;
    }


    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> false);
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

    @Test
    @Transactional
    public void testGetBoardWorksWithEmptyBoard() {
        Board testBoard = createTestBoard();
        BoardDetailDto response = boardsController.getBoard(testBoard.getId());

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
        testTodoTask1.setBoardColumn(testBoard.getBoardColumns().get(0));
        taskRepository.save(testTodoTask1);

        Task testTodoTask2 = new Task();
        testTodoTask2.setTitle("Test Task 2");
        testTodoTask2.setDescription("Testing");
        testTodoTask2.setBoardColumn(testBoard.getBoardColumns().get(0));
        taskRepository.save(testTodoTask2);

        Task testCompletedTask = new Task();
        testCompletedTask.setTitle("Test Completed 1");
        testCompletedTask.setDescription("A completed task");
        testCompletedTask.setBoardColumn(testBoard.getBoardColumns().get(2));
        taskRepository.save(testCompletedTask);

        BoardDetailDto response = boardsController.getBoard(testBoard.getId());

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

    @Test
    @Transactional
    public void testDeleteBoard_setsTaskColumnToNullByDefault() {
        Organization testOrganization = new Organization();
        testOrganization.setDisplayName("Test Organization");
        organizationRepository.save(testOrganization);

        Board testBoard = createTestBoard();
        Task testTodoTask1 = new Task();
        testTodoTask1.setTitle("Test Task 1");
        testTodoTask1.setDescription("Testing");
        testTodoTask1.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(0).addTask(testTodoTask1);
        taskRepository.save(testTodoTask1);

        Task testTodoTask2 = new Task();
        testTodoTask2.setTitle("Test Task 2");
        testTodoTask2.setDescription("Testing");
        testTodoTask2.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(0).addTask(testTodoTask2);
        taskRepository.save(testTodoTask2);

        Task testCompletedTask = new Task();
        testCompletedTask.setTitle("Test Completed 1");
        testCompletedTask.setDescription("A completed task");
        testCompletedTask.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(2).addTask(testCompletedTask);
        taskRepository.save(testCompletedTask);

        List<Task> boardTasks = taskRepository.findByBoardId(testBoard.getId());

        boardsController.deleteBoard(testBoard.getId(), false);

        for (Task task : boardTasks) {
            entityManager.refresh(task);
            assert task.getBoardColumn() == null;
        }
    }

    @Test
    @Transactional
    public void testDeleteBoard_deletesTasksIfDeleteTasksIsTrue() {
        Organization testOrganization = new Organization();
        testOrganization.setDisplayName("Test Organization");
        organizationRepository.save(testOrganization);

        Board testBoard = createTestBoard();
        Task testTodoTask1 = new Task();
        testTodoTask1.setTitle("Test Task 1");
        testTodoTask1.setDescription("Testing");
        testTodoTask1.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(0).addTask(testTodoTask1);
        taskRepository.save(testTodoTask1);

        Task testTodoTask2 = new Task();
        testTodoTask2.setTitle("Test Task 2");
        testTodoTask2.setDescription("Testing");
        testTodoTask2.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(0).addTask(testTodoTask2);
        taskRepository.save(testTodoTask2);

        Task testCompletedTask = new Task();
        testCompletedTask.setTitle("Test Completed 1");
        testCompletedTask.setDescription("A completed task");
        testCompletedTask.setOrganization(testOrganization);
        testBoard.getBoardColumns().get(2).addTask(testCompletedTask);
        taskRepository.save(testCompletedTask);

        List<Task> boardTasks = taskRepository.findByBoardId(testBoard.getId());

        boardsController.deleteBoard(testBoard.getId(), true);

        for (Task task : boardTasks) {
            assert taskRepository.findById(task.getId()).isEmpty();
        }
    }

    @Test
    @Transactional
    public void test_updateBoardHeade_returnsUpdatedBoard() {
        Board testBoard = createTestBoard();
        UpdateBoardHeaderDTO requestBody = new UpdateBoardHeaderDTO("New Title 23");
        assert !testBoard.getTitle().equals(requestBody.title());
        BoardDetailDto response = boardsController.updateBoardHeader(testBoard.getId(), requestBody);

        assert response.id().equals(testBoard.getId());
        assert response.title().equals(requestBody.title());
    }

}