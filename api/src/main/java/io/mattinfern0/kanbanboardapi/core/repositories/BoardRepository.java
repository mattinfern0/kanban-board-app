package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    List<Board> findAllByOrganizationId(UUID organizationId);
}
