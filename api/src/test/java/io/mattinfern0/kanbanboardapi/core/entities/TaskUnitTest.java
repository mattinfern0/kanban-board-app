package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
class TaskUnitTest {
    @Test
    void setBoardColumn_setsProperty() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();

        testTask.setBoardColumn(testColumn);
        assert testTask.getBoardColumn().equals(testColumn);
    }

    @Test
    void setBoardColumn_addsTaskToTaskList_boardColumnNotNull() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();

        testTask.setBoardColumn(testColumn);
        assert testColumn.getTasks().contains(testTask);
    }

    @Test
    void setBoard_removesColumnFromOldBoardColumnList_oldBoardNotNull() {
        BoardColumn testColumn = new BoardColumn();
        BoardColumn oldColumn = new BoardColumn();
        Task testTask = new Task();
        testTask.setBoardColumn(oldColumn);
        testTask.setBoardColumn(testColumn);
        assert !oldColumn.getTasks().contains(testTask);
    }

}