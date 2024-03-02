package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
class TaskUnitTest {
    @Test
    void setTaskStatus_throwsError_ifStatusNotMatchColumnAndColumnIsNotNull() {
        BoardColumn testColumn = new BoardColumn();
        TaskStatus testTaskStatus = new TaskStatus();
        testColumn.setTaskStatus(testTaskStatus);

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