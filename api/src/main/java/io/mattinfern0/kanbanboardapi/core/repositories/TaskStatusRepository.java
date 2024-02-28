package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.TaskStatus;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, UUID> {
    Optional<TaskStatus> findByCodename(TaskStatusCode codename);
}
