package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
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
    final UserAccessService userAccessService;

    final OrganizationDtoMapper organizationDtoMapper;

    public OrganizationService(
        OrganizationRepository organizationRepository,
        OrganizationMembershipRepository organizationMembershipRepository,
        UserAccessService userAccessService,
        OrganizationDtoMapper organizationDtoMapper
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
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

    public OrganizationDetailsDto getOrganizationDetails(Principal principal, UUID organizationId) {
        Organization organization = organizationRepository
            .findById(organizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization with id not found"));

        if (!userAccessService.canAccessOrganization(principal, organizationId)) {
            throw new AccessDeniedException("You do not have access to this organization");
        }

        return organizationDtoMapper.organizationEntitytoDetailsDto(organization);
    }
}
