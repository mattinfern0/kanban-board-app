package io.mattinfern0.kanbanboardapi.core.repositories;

import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, UUID> {
    @Query("""
            SELECT invite
            FROM OrganizationInvite invite
            WHERE
                lower(invite.email) = lower(:email)
                AND invite.organization.id = :organizationId
                And invite.status = 1
                AND invite.expiresAt > CURRENT_TIMESTAMP
        """)
    Optional<OrganizationInvite> findActiveInviteByEmailAndOrganizationId(String email, UUID organizationId);

    @Query("""
            SELECT invite
            FROM OrganizationInvite invite
            WHERE
                invite.organization.id = :organizationId
                And invite.status = 1
                AND invite.expiresAt > CURRENT_TIMESTAMP
        """)
    List<OrganizationInvite> findValidByOrganizationId(UUID organizationId);

    @Query("""
            SELECT invite
            FROM OrganizationInvite invite
            WHERE
                lower(invite.email) = lower(:email)
                And invite.status = 1
                AND invite.expiresAt > CURRENT_TIMESTAMP
        """)
    List<OrganizationInvite> findPendingByEmail(String email);


    Optional<OrganizationInvite> findByToken(String token);
}
