package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    @Query("""
        SELECT organization
        from Organization organization
        join OrganizationMembership membership ON organization.id = membership.pk.organizationId
        WHERE membership.role = 1 AND membership.pk.userId = ?1
    """)
    Optional<Organization> findPersonalOrganization(UUID userId);
}
