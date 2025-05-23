package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.IllegalOperationException;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationMemberDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.UpdateMembershipDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.OrganizationDtoMapper;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;
    final UserRepository userRepository;
    final UserAccessService userAccessService;

    final OrganizationDtoMapper organizationDtoMapper;

    public OrganizationService(
        OrganizationRepository organizationRepository,
        OrganizationMembershipRepository organizationMembershipRepository, UserRepository userRepository,
        UserAccessService userAccessService,
        OrganizationDtoMapper organizationDtoMapper
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.userRepository = userRepository;
        this.userAccessService = userAccessService;
        this.organizationDtoMapper = organizationDtoMapper;
    }

    @Transactional
    public Organization createPersonalOrganization(User user) {
        Organization personalOrganization = new Organization();
        personalOrganization.setPersonalForUser(user);
        personalOrganization.setDisplayName(String.format("Personal - User %s", user.getId()));
        organizationRepository.saveAndFlush(personalOrganization);

        OrganizationMembership organizationMembership = new OrganizationMembership(personalOrganization, user);
        organizationMembership.setRole(OrganizationRole.OWNER);

        organizationMembershipRepository.saveAndFlush(organizationMembership);
        return personalOrganization;
    }

    @Transactional
    public OrganizationMembership addUserToOrganization(
        Principal principal,
        UUID organizationId,
        OrganizationRole role
    ) {
        Organization organization = organizationRepository
            .findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization with id not found"));

        User user = userRepository
            .findByFirebaseId(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OrganizationMembership membership = new OrganizationMembership(organization, user);
        membership.setRole(role);
        return organizationMembershipRepository.saveAndFlush(membership);
    }

    public OrganizationDetailsDto getOrganizationDetails(Principal principal, UUID organizationId) {
        Organization organization = organizationRepository
            .findById(organizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization with id not found"));

        if (!userAccessService.canAccessOrganization(principal, organizationId)) {
            throw new AccessDeniedException("You do not have access to this organization");
        }

        return organizationDtoMapper.organizationEntitytoDetailsDto(organization);
    }


    @Transactional
    public OrganizationMemberDto updateMembership(
        Principal principal,
        UUID organizationId,
        UUID userId,
        UpdateMembershipDto updateMembershipDto
    ) {
        OrganizationMembership principalMembership = userAccessService.getMembership(principal, organizationId)
            .orElseThrow(() -> new AccessDeniedException("You do not have access to this organization"));

        if (!principalMembership.getRole().equals(OrganizationRole.OWNER)) {
            throw new AccessDeniedException("You do not have permission to update memberships in this organization");
        }

        OrganizationMembershipPk membershipPk = new OrganizationMembershipPk(organizationId, userId);
        OrganizationMembership targetMembership = organizationMembershipRepository
            .findById(membershipPk)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        OrganizationRole oldRole = targetMembership.getRole();
        OrganizationRole newRole = updateMembershipDto.role();
        if (oldRole.equals(OrganizationRole.OWNER) && newRole.equals(OrganizationRole.MEMBER)) {
            if (organizationMembershipRepository.countMembersWithRoleInOrganization(organizationId, OrganizationRole.OWNER) <= 1) {
                throw new IllegalOperationException("At least 1 member must be an owner in the organization");
            }
        }

        targetMembership.setRole(newRole);
        organizationMembershipRepository.saveAndFlush(targetMembership);

        return organizationDtoMapper.organizationMembershipEntityToDto(targetMembership);
    }


    @Transactional
    public void deleteMembership(
        Principal principal,
        UUID organizationId,
        UUID userId
    ) {
        OrganizationMembership principalMembership = userAccessService.getMembership(principal, organizationId)
            .orElseThrow(() -> new AccessDeniedException("You do not have access to this organization"));

        if (!(
            principalMembership.getRole().equals(OrganizationRole.OWNER)
                || principalMembership.getUser().getId().equals(userId))
        ) {
            throw new AccessDeniedException("You do not have permission to delete this membership");
        }

        OrganizationMembershipPk membershipPk = new OrganizationMembershipPk(organizationId, userId);
        OrganizationMembership targetMembership = organizationMembershipRepository
            .findById(membershipPk)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (targetMembership.getRole().equals(OrganizationRole.OWNER) && organizationMembershipRepository.countMembersWithRoleInOrganization(organizationId, OrganizationRole.OWNER) <= 1) {
            throw new IllegalOperationException("You cannot delete the last owner of the organization");
        }

        organizationMembershipRepository.delete(targetMembership);
    }
}
