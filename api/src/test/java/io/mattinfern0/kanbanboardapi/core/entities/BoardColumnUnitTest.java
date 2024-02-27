package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        Task testTask = new Task();

        testColumn.addTask(testTask);

        assert testTask.getBoardColumn().equals(testColumn);
    }

    @Test
    void addTask_addsTaskToTaskList() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();

        testColumn.addTask(testTask);

        assert testColumn.getTasks().contains(testTask);
    }

    @Test
    void addTask_removesOldColumn_taskAlreadyHasAColumn() {
        BoardColumn testColumn = new BoardColumn();
        BoardColumn oldColumn = new BoardColumn();
        Task testTask = new Task();
        testTask.setBoardColumn(oldColumn);

        testColumn.addTask(testTask);

        assert !testTask.getBoardColumn().equals(oldColumn);
        assert !oldColumn.getTasks().contains(testTask);
    }

    @Test
    void removeTask_setsProperty() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();
        testColumn.addTask(testTask);

        testColumn.removeTask(testTask);

        assert testTask.getBoardColumn() == null;
    }

    @Test
    void removeTask_removesTaskToTaskList() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();
        testColumn.addTask(testTask);

        testColumn.removeTask(testTask);

        assert !testColumn.getTasks().contains(testTask);
    }
}