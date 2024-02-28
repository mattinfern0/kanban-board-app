package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskStatusRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class TaskStatusServiceUnitTest {

    @InjectMocks
    TaskStatusService taskStatusService;

    @Mock
    TaskStatusRepository taskStatusRepository;

    @Test
    void findOrCreate_createsNewRecordIfRecordWithCodenameNotExists() {
        TaskStatusCode testCode = TaskStatusCode.OTHER;
        Mockito.when(taskStatusRepository.findByCodename(testCode)).thenReturn(Optional.empty());

        taskStatusService.findOrCreate(testCode);
    }

    @Test
    void findOrCreate_returnsRecordIfRecordWithCodenameExists() {
        TaskStatusCode testCode = TaskStatusCode.OTHER;
        TaskStatus existingRecord = new TaskStatus();
        existingRecord.setId(UUID.randomUUID());
        existingRecord.setCodename(testCode);
        Mockito.when(taskStatusRepository.findByCodename(testCode)).thenReturn(Optional.of(existingRecord));

        TaskStatus result = taskStatusService.findOrCreate(testCode);
        assert result.getId().equals(existingRecord.getId());
    }
}