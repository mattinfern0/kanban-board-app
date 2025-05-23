package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {
}
