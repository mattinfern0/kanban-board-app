package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
}
