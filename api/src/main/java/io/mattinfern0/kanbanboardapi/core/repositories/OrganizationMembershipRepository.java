package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, OrganizationMembershipPk> {
    public boolean existsByPk(OrganizationMembershipPk organizationMembershipPk);

    @Query(
        "SELECT COUNT(m) FROM OrganizationMembership m WHERE m.pk.organizationId = :organizationId AND m.role = 1"
    )
    Integer countOwnersInOrganization(UUID organizationId);
}
