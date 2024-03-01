package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        TaskStatus testStatus = new TaskStatus();
        BoardColumn testColumn = new BoardColumn();
        testColumn.setTaskStatus(testStatus);
        BoardColumn oldColumn = new BoardColumn();
        oldColumn.setTaskStatus(testStatus);


        Task testTask = new Task();
        testTask.setBoardColumn(oldColumn);
        testTask.setBoardColumn(testColumn);
        assert !oldColumn.getTasks().contains(testTask);
    }

    @Test
    void setBoardColumn_setsTaskStatusToColumnsStatus_ifColumnNotNull() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();
        TaskStatus oldStatus = new TaskStatus();
        testTask.setTaskStatus(oldStatus);


        TaskStatus columnStatus = new TaskStatus();
        testColumn.setTaskStatus(columnStatus);

        testTask.setBoardColumn(testColumn);
        assert testTask.getTaskStatus().equals(testColumn.getTaskStatus());
    }

    @Test
    void setBoardColumn_doesNotChangeStatus_ifColumnIsNull() {
        Task testTask = new Task();
        TaskStatus oldStatus = new TaskStatus();
        testTask.setTaskStatus(oldStatus);

        testTask.setBoardColumn(null);
        assert testTask.getTaskStatus().equals(oldStatus);
    }

    @Test
    void setTaskStatus_throwsError_ifStatusNotMatchColumnAndColumnIsNotNull() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();

        TaskStatus otherStatus = new TaskStatus();
        TaskStatus columnStatus = new TaskStatus();
        testColumn.setTaskStatus(columnStatus);
        testTask.setBoardColumn(testColumn);

        assertThrows(IllegalArgumentException.class, () -> {
            testTask.setTaskStatus(otherStatus);
        });
    }

    @Test
    void setTaskStatus_doesNotThrowError_ifStatusMatchesColumnAndColumnIsNotNull() {
        BoardColumn testColumn = new BoardColumn();
        Task testTask = new Task();

        TaskStatus columnStatus = new TaskStatus();
        testColumn.setTaskStatus(columnStatus);
        testTask.setBoardColumn(testColumn);

        testTask.setTaskStatus(columnStatus);
    }

    @Test
    void setTaskStatus_setsStatus_ifColumnIsNull() {
        Task testTask = new Task();
        testTask.setBoardColumn(null);

        TaskStatus oldStatus = new TaskStatus();
        testTask.setTaskStatus(oldStatus);

        TaskStatus newStatus = new TaskStatus();
        testTask.setTaskStatus(newStatus);
        assert testTask.getTaskStatus().equals(newStatus);
    }

}