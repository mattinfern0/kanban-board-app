package io.mattinfern0.kanbanboardapi.tasks;

import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskStatusService {
    final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Transactional
    public TaskStatus findOrCreate(TaskStatusCode taskStatusCode) {
        return taskStatusRepository.findByCodename(taskStatusCode).orElseGet(() -> {
            TaskStatus newRecord = new TaskStatus();
            newRecord.setCodename(taskStatusCode);
            taskStatusRepository.save(newRecord);
            return newRecord;
        });
    }
}
