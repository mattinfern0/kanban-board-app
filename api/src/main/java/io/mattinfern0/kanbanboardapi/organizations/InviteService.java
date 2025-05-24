package io.mattinfern0.kanbanboardapi.organizations;

import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteeListItemDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import io.mattinfern0.kanbanboardapi.users.FirebaseUserService;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InviteService {
    final OrganizationRepository organizationRepository;
    final OrganizationInviteRepository organizationInviteRepository;

    final InviteDtoMapper inviteDtoMapper;
    private final FirebaseUserService firebaseUserService;
    final UserAccessService userAccessService;

    @Autowired
    public InviteService(OrganizationRepository organizationRepository, OrganizationMembershipRepository organizationMembershipRepository, UserRepository userRepository, OrganizationInviteRepository organizationInviteRepository, InviteDtoMapper inviteDtoMapper, FirebaseUserService firebaseUserService, UserAccessService userAccessService) {
        this.organizationRepository = organizationRepository;
        this.organizationInviteRepository = organizationInviteRepository;
        this.inviteDtoMapper = inviteDtoMapper;
        this.firebaseUserService = firebaseUserService;
        this.userAccessService = userAccessService;
    }

    public InviteDetailDto createInvite(Principal principal, CreateInviteDto createInviteDto) {
        Organization organization = organizationRepository
            .findById(createInviteDto.organizationId())
            .orElseThrow(() -> new IllegalArgumentException("Organization with id not found"));

        OrganizationMembership principalMembership = userAccessService.getMembership(principal, createInviteDto.organizationId())
            .orElseThrow(() -> new AccessDeniedException("You do not have permission to invite users to this organization"));

        if (principalMembership.getRole() != OrganizationRole.OWNER) {
            throw new AccessDeniedException("You do not have permission to invite users to this organization");
        }

        Optional<OrganizationInvite> existingInvite = organizationInviteRepository.findValidInviteByEmailAndOrganizationId(
            createInviteDto.email(),
            createInviteDto.organizationId()
        );
        if (existingInvite.isPresent()) {
            throw new IllegalArgumentException("Invitee has already been invited organization");
        }


        /*
            TODO CHecks:
            - Check if the invitee is a member of the organization
            - Check if the invitee already has a pending invite
        * */

        OrganizationInvite inviteEntity = new OrganizationInvite();
        inviteEntity.setEmail(createInviteDto.email());
        inviteEntity.setId(UUID.randomUUID());
        inviteEntity.setOrganization(organization);
        inviteEntity.setStatus(OrganizationInviteStatus.PENDING);

        // TODO: There may be a more secure way to generate this token
        inviteEntity.setToken(UUID.randomUUID().toString());

        // TODO: Set expiration date for the invite

        organizationInviteRepository.save(inviteEntity);

        return inviteDtoMapper.entityToDetailDto(inviteEntity);
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

    public List<InviteeListItemDto> getOrganizationInvitees(Principal principal, UUID organizationId) {
        List<OrganizationInvite> entites = organizationInviteRepository.findValidByOrganizationId(organizationId);
        return inviteDtoMapper.entityListToInviteeList(entites);
    }

    boolean isInviteForUser(OrganizationInvite invite, Principal principal) {
        UserRecord firebaseUserDetails = firebaseUserService.getUserDetails(principal);
        return invite.getEmail().equals(firebaseUserDetails.getEmail());
    }
}
