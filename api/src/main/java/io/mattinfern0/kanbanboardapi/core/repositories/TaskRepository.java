package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
