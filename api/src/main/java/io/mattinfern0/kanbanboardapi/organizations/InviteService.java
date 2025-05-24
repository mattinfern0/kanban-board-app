package io.mattinfern0.kanbanboardapi.organizations;

import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import io.mattinfern0.kanbanboardapi.users.FirebaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
public class InviteService {
    final OrganizationRepository organizationRepository;
    final OrganizationInviteRepository organizationInviteRepository;

    final InviteDtoMapper inviteDtoMapper;
    private final FirebaseUserService firebaseUserService;

    @Autowired
    public InviteService(OrganizationRepository organizationRepository, OrganizationInviteRepository organizationInviteRepository, InviteDtoMapper inviteDtoMapper, FirebaseUserService firebaseUserService) {
        this.organizationRepository = organizationRepository;
        this.organizationInviteRepository = organizationInviteRepository;
        this.inviteDtoMapper = inviteDtoMapper;
        this.firebaseUserService = firebaseUserService;
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

        // TODO: There may be a more secure way to generate this token
        inviteEntity.setToken(UUID.randomUUID().toString());

        // TODO: Set expiration date for the invite

        organizationInviteRepository.save(inviteEntity);

        return inviteDtoMapper.entityToDto(inviteEntity);
    }

    public void acceptInvite(Principal principal, String inviteToken) {
        OrganizationInvite invite = organizationInviteRepository
            .findByToken(inviteToken)
            .orElseThrow(() -> new IllegalArgumentException("Invite with token not found"));

        if (!isInviteForUser(invite, principal)) {
            throw new AccessDeniedException("This invite is not for you");
        }

        if (invite.getStatus() != OrganizationInviteStatus.PENDING) {
            throw new IllegalStateException("Invite is not pending");
        }

        invite.setStatus(OrganizationInviteStatus.ACCEPTED);
        organizationInviteRepository.save(invite);

        // TODO: Add user to the organization as a member
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

    boolean isInviteForUser(OrganizationInvite invite, Principal principal) {
        UserRecord firebaseUserDetails = firebaseUserService.getUserDetails(principal);
        return invite.getEmail().equals(firebaseUserDetails.getEmail());
    }
}
