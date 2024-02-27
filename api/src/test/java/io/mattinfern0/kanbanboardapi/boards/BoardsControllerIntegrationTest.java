package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Tag("IntegrationTest")
public class BoardsControllerIntegrationTest {
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-bullseye");

    final OrganizationRepository organizationRepository;

    final BoardRepository boardRepository;

    final BoardColumnRepository boardColumnRepository;

    final TaskRepository taskRepository;

    final EntityManager entityManager;

    final BoardsController boardsController;

    @Autowired
    public BoardsControllerIntegrationTest(OrganizationRepository organizationRepository, BoardRepository boardRepository, BoardColumnRepository boardColumnRepository, TaskRepository taskRepository, EntityManager entityManager, BoardsController boardsController) {
        this.organizationRepository = organizationRepository;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.taskRepository = taskRepository;
        this.entityManager = entityManager;
        this.boardsController = boardsController;
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
    }

    @BeforeEach
    void beforeEach() {
        clearRepositories();
    }

    void clearRepositories() {
        organizationRepository.deleteAll();
        boardRepository.deleteAll();
        boardColumnRepository.deleteAll();
        taskRepository.deleteAll();
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

        BoardColumn testInProgressColumn = new BoardColumn();
        testInProgressColumn.setBoard(testBoard);
        testInProgressColumn.setDisplayOrder(1);
        testInProgressColumn.setTitle("Todo");

        BoardColumn testCompletedColumn = new BoardColumn();
        testCompletedColumn.setBoard(testBoard);
        testCompletedColumn.setDisplayOrder(1);
        testCompletedColumn.setTitle("Completed");

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

        assert response.getId().equals(testBoard.getId());
        assert response.getTitle().equals(testBoard.getTitle());

        for (int i = 0; i < testBoard.getBoardColumns().size(); i++) {
            BoardColumn entity = testBoard.getBoardColumns().get(i);
            BoardColumnDto dto = response.getBoardColumns().get(i);
            assert entity.getId().equals(dto.getId());
            assert entity.getTitle().equals(dto.getTitle());
        }
    }

}