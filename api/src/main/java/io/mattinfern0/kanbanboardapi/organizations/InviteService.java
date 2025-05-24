package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
public class InviteService {
    final OrganizationRepository organizationRepository;
    final OrganizationInviteRepository organizationInviteRepository;

    final InviteDtoMapper inviteDtoMapper;

    @Autowired
    public InviteService(OrganizationRepository organizationRepository, OrganizationInviteRepository organizationInviteRepository, InviteDtoMapper inviteDtoMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationInviteRepository = organizationInviteRepository;
        this.inviteDtoMapper = inviteDtoMapper;
    }

    public InviteDto createInvite(CreateInviteDto createInviteDto) {
        Organization organization = organizationRepository
            .findById(createInviteDto.organizationId())
            .orElseThrow(() -> new IllegalArgumentException("Organization with id not found"));

        OrganizationInvite inviteEntity = new OrganizationInvite();
        inviteEntity.setEmail(createInviteDto.email());
        inviteEntity.setId(UUID.randomUUID());
        inviteEntity.setOrganization(organization);
        inviteEntity.setStatus(OrganizationInviteStatus.PENDING);

        organizationInviteRepository.save(inviteEntity);

        return inviteDtoMapper.entityToDto(inviteEntity);
    }

    public void acceptInvite(Principal principal, String inviteToken) {
        OrganizationInvite invite = organizationInviteRepository
            .findByToken(inviteToken)
            .orElseThrow(() -> new IllegalArgumentException("Invite with token not found"));

        if (invite.getStatus() != OrganizationInviteStatus.PENDING) {
            throw new IllegalStateException("Invite is not pending");
        }

        invite.setStatus(OrganizationInviteStatus.ACCEPTED);
        organizationInviteRepository.save(invite);
    }

    public void revokeInvite(UUID inviteId) {
        OrganizationInvite invite = organizationInviteRepository
            .findById(inviteId)
            .orElseThrow(() -> new IllegalArgumentException("Invite with id not found"));

        if (invite.getStatus() != OrganizationInviteStatus.PENDING) {
            throw new IllegalStateException("Invite is not pending");
        }

        invite.setStatus(OrganizationInviteStatus.REVOKED);
        organizationInviteRepository.save(invite);
    }
}
