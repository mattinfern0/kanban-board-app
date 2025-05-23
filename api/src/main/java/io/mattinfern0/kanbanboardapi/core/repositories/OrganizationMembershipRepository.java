package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, OrganizationMembershipPk> {
    boolean existsByPk(OrganizationMembershipPk organizationMembershipPk);

    @Query(
        "SELECT COUNT(m) FROM OrganizationMembership m WHERE m.pk.organizationId = :organizationId AND m.role = :role"
    )
    Integer countMembersWithRoleInOrganization(UUID organizationId, OrganizationRole role);

    boolean existsByUserAndOrganization(User user, Organization organization);
}
