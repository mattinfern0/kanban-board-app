package io.mattinfern0.kanbanboardapi.organizations;

import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.IllegalOperationException;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteeListItemDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import io.mattinfern0.kanbanboardapi.users.FirebaseUserService;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InviteService {
    final OrganizationRepository organizationRepository;
    final OrganizationInviteRepository organizationInviteRepository;
    final OrganizationMembershipRepository organizationMembershipRepository;

    final InviteDtoMapper inviteDtoMapper;
    private final FirebaseUserService firebaseUserService;
    final UserAccessService userAccessService;
    private final OrganizationService organizationService;

    final Clock clock;

    static final Integer INVITE_EXPIRATION_DAYS = 7;
    static final Integer INVITE_TOKEN_LENGTH = 20;

    @Autowired
    public InviteService(OrganizationRepository organizationRepository, OrganizationInviteRepository organizationInviteRepository, OrganizationMembershipRepository organizationMembershipRepository1, InviteDtoMapper inviteDtoMapper, FirebaseUserService firebaseUserService, UserAccessService userAccessService, OrganizationService organizationService, Clock clock) {
        this.organizationRepository = organizationRepository;
        this.organizationInviteRepository = organizationInviteRepository;
        this.organizationMembershipRepository = organizationMembershipRepository1;
        this.inviteDtoMapper = inviteDtoMapper;
        this.firebaseUserService = firebaseUserService;
        this.userAccessService = userAccessService;
        this.organizationService = organizationService;
        this.clock = clock;
    }

    @Transactional
    public InviteDetailDto createInvite(Principal principal, CreateInviteDto createInviteDto) {
        Organization organization = organizationRepository
            .findById(createInviteDto.organizationId())
            .orElseThrow(() -> new IllegalArgumentException("Organization with id not found"));

        OrganizationMembership principalMembership = userAccessService.getMembership(principal, createInviteDto.organizationId())
            .orElseThrow(() -> new AccessDeniedException("You do not have permission to invite users to this organization"));

        if (principalMembership.getRole() != OrganizationRole.OWNER) {
            throw new AccessDeniedException("You do not have permission to invite users to this organization");
        }

        Optional<OrganizationInvite> existingInvite = organizationInviteRepository.findActiveInviteByEmailAndOrganizationId(
            createInviteDto.email(),
            createInviteDto.organizationId()
        );
        if (existingInvite.isPresent()) {
            throw new IllegalArgumentException("Invitee has already been invited to the organization");
        }

        Optional<User> inviteeUser = firebaseUserService.getUserEntityByEmail(createInviteDto.email());
        if (inviteeUser.isPresent() && organizationMembershipRepository.existsByUserAndOrganization(inviteeUser.get(), organization)) {
            throw new IllegalArgumentException("Invitee is already a member of the organization");
        }

        OrganizationInvite inviteEntity = new OrganizationInvite();
        inviteEntity.setEmail(createInviteDto.email());
        inviteEntity.setId(UUID.randomUUID());
        inviteEntity.setOrganization(organization);
        inviteEntity.setStatus(OrganizationInviteStatus.PENDING);

        inviteEntity.setToken(generateInviteToken());

        inviteEntity.setExpiresAt(ZonedDateTime.now().plusDays(INVITE_EXPIRATION_DAYS));

        organizationInviteRepository.save(inviteEntity);

        return inviteDtoMapper.entityToDetailDto(inviteEntity);
    }

    @Transactional
    public void acceptInvite(Principal principal, String inviteToken) {
        OrganizationInvite invite = organizationInviteRepository
            .findByToken(inviteToken)
            .orElseThrow(() -> new ResourceNotFoundException("Invite with token not found"));

        if (!isInviteForUser(invite, principal)) {
            throw new AccessDeniedException("This invite is not for you");
        }

        if (invite.getStatus() != OrganizationInviteStatus.PENDING) {
            throw new IllegalOperationException("Invite is not pending");
        }

        invite.setStatus(OrganizationInviteStatus.ACCEPTED);

        if (ZonedDateTime.now(clock).isAfter(invite.getExpiresAt())) {
            throw new IllegalOperationException("Invite has expired");
        }

        organizationInviteRepository.save(invite);

        organizationService.addUserToOrganization(principal, invite.getOrganization().getId(), OrganizationRole.MEMBER);
    }

    @Transactional
    public void revokeInvite(UUID inviteId) {
        OrganizationInvite invite = organizationInviteRepository
            .findById(inviteId)
            .orElseThrow(() -> new IllegalArgumentException("Invite with id not found"));

        if (invite.getStatus() != OrganizationInviteStatus.PENDING) {
            throw new IllegalOperationException("Invite is not pending");
        }

        invite.setStatus(OrganizationInviteStatus.REVOKED);
        organizationInviteRepository.save(invite);
    }

    public List<InviteeListItemDto> getOrganizationInvitees(Principal principal, UUID organizationId) {
        List<OrganizationInvite> entites = organizationInviteRepository.findValidByOrganizationId(organizationId);
        return inviteDtoMapper.entityListToInviteeList(entites);
    }

    public List<InviteDetailDto> getCurrentPrincipalInvites(Principal principal) {
        UserRecord firebaseUserDetails = firebaseUserService.getUserDetails(principal);
        String email = firebaseUserDetails.getEmail();

        List<OrganizationInvite> invites = organizationInviteRepository.findPendingByEmail(email);
        return inviteDtoMapper.entityListToDetailDtoList(invites);
    }

    boolean isInviteForUser(OrganizationInvite invite, Principal principal) {
        UserRecord firebaseUserDetails = firebaseUserService.getUserDetails(principal);
        return invite.getEmail().equalsIgnoreCase(firebaseUserDetails.getEmail());
    }

    private String generateInviteToken() {
        // Based off of https://stackoverflow.com/a/50381020
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[INVITE_TOKEN_LENGTH];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
