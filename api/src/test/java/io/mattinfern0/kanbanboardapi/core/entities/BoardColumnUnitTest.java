package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Tag("UnitTest")
class BoardColumnUnitTest {

    @Test
    void setBoard_setsProperty() {
        Board testBoard = new Board();
        BoardColumn testColumn = new BoardColumn();

        testColumn.setBoard(testBoard);
        assert testColumn.getBoard().equals(testBoard);
    }

    @Test
    void setBoard_addsColumnToBoardColumnList_boardNotNull() {
        Board testBoard = new Board();
        BoardColumn testColumn = new BoardColumn();

        testColumn.setBoard(testBoard);
        assert testBoard.getBoardColumns().contains(testColumn);
    }

    @Test
    void setBoard_removesColumnFromOldBoardColumnList_oldBoardNotNull() {
        Board testBoard = new Board();
        Board oldBoard = new Board();
        BoardColumn testColumn = new BoardColumn();
        testColumn.setBoard(oldBoard);
        testColumn.setBoard(testBoard);
        assert !oldBoard.getBoardColumns().contains(testColumn);
    }

    @Test
    void addTask_setsProperty() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();

        testColumn.addTask(testTask);

        assert testTask.getBoardColumn().equals(testColumn);
    }

    @Test
    void addTask_addsTaskToTaskList() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();

        testColumn.addTask(testTask);

        assert testColumn.getTasks().contains(testTask);
    }

    @Test
    void addTask_syncsTaskStatusWithColumn() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());

        for (int i = 0; i < 3; i++) {
            testColumn.addTask(new Task());
        }

        Task testTask = new Task();
        testColumn.addTask(testTask);

        assert testTask.getTaskStatus().equals(testColumn.getTaskStatus());
    }

    @Test
    void addTask_setsTaskColumnOrderToLast() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());

        Random r = new Random();

        for (int i = 0; i < 3; i++) {
            Task existingTask = new Task();
            testColumn.addTask(existingTask);
        }


        Task testTask = new Task();
        testColumn.addTask(testTask);

        List<Task> resultTasks = testColumn.getTasks();

        resultTasks.sort(Comparator.comparingInt(Task::getBoardColumnOrder));

        assert Objects.equals(testTask, resultTasks.get(resultTasks.size() - 1));
    }

    @Test
    void addTask_setsTaskColumnOrderToLastEvenWithWeirdColumnOrders() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());

        Random r = new Random();

        for (int i = 0; i < 3; i++) {
            Task existingTask = new Task();
            testColumn.addTask(existingTask);
        }
        // Column order values may be arbitrary
        testColumn.getTasks().forEach((t) -> {
            t.setBoardColumnOrder(Math.abs(r.nextInt()));
        });

        Task testTask = new Task();
        testColumn.addTask(testTask);

        List<Task> resultTasks = testColumn.getTasks();

        resultTasks.sort(Comparator.comparingInt(Task::getBoardColumnOrder));

        assert Objects.equals(testTask, resultTasks.get(resultTasks.size() - 1));
    }

    @Test
    void addTask_removesOldColumn_taskAlreadyHasAColumn() {
        TaskStatus testStatus = new TaskStatus();

        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(testStatus);

        BoardColumn oldColumn = new BoardColumn();
        oldColumn.setTaskStatus(testStatus);

        Task testTask = new Task();
        testTask.setBoardColumn(oldColumn);

        testColumn.addTask(testTask);

        assert !testTask.getBoardColumn().equals(oldColumn);
        assert !oldColumn.getTasks().contains(testTask);
    }

    @Test
    void insertTask_insertsTaskAtCorrectPositionInList() {
        TaskStatus testStatus = new TaskStatus();

        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(testStatus);

        for (int i = 0; i < 4; i++) {
            Task existingTask = new Task();
            testColumn.addTask(existingTask);
        }

        Task testTask = new Task();
        testColumn.insertTask(testTask, 1);

        assert Objects.equals(testTask.getBoardColumnOrder(), 1);
        assert testColumn.getTasks().get(1).equals(testTask);
    }

    @Test
    void removeTask_setsProperty() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();
        testColumn.addTask(testTask);

        testColumn.removeTask(testTask);

        assert testTask.getBoardColumn() == null;
    }

    @Test
    void removeTask_removesTaskToTaskList() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();
        testColumn.addTask(testTask);

        testColumn.removeTask(testTask);

        assert !testColumn.getTasks().contains(testTask);
    }

    @Test
    void removeTask_setsTaskColumnOrderToNull() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();
        testColumn.addTask(testTask);

        testColumn.removeTask(testTask);

        assert testTask.getBoardColumnOrder() == null;
    }

    @Test
    void removeTask_keepsTaskStatus() {
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(new TaskStatus());
        Task testTask = new Task();
        testColumn.addTask(testTask);

        TaskStatus oldStatus = testTask.getTaskStatus();

        testColumn.removeTask(testTask);

        assert testTask.getTaskStatus().equals(oldStatus);
    }
}