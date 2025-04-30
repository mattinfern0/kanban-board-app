package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;

    public OrganizationService(
        OrganizationRepository organizationRepository,
        OrganizationMembershipRepository organizationMembershipRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
    }

    @Transactional
    public Organization createPersonalOrganization(User user) {
        Organization personalOrganization = new Organization();
        personalOrganization.setPersonalForUser(user);
        personalOrganization.setDisplayName(String.format("Personal - User %s", user.getId()));
        organizationRepository.saveAndFlush(personalOrganization);


        OrganizationMembershipPk organizationMembershipPk = new OrganizationMembershipPk();
        organizationMembershipPk.setOrganizationId(personalOrganization.getId());
        organizationMembershipPk.setUserId(user.getId());

        OrganizationMembership organizationMembership = new OrganizationMembership();
        organizationMembership.setPk(organizationMembershipPk);
        organizationMembership.setRole(OrganizationRole.OWNER);

        organizationMembershipRepository.saveAndFlush(organizationMembership);
        return personalOrganization;
    }
}
