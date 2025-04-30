package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {
    @InjectMocks
    OrganizationService organizationService;

    @Mock
    OrganizationRepository organizationRepository;

    @Mock
    OrganizationMembershipRepository organizationMembershipRepository;

    @Test
    void createPersonalOrganization_creates_correct_organization_record() {
        ArgumentCaptor<Organization> organizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);
        User someUser = new User();

        organizationService.createPersonalOrganization(someUser);

        Mockito.verify(organizationRepository).saveAndFlush(organizationArgumentCaptor.capture());
        Organization savedOrganization = organizationArgumentCaptor.getValue();
        Assertions.assertEquals(savedOrganization.getPersonalForUser(), someUser);
    }

    @Test
    void createPersonalOrganization_creates_correct_membership_record() {
        ArgumentCaptor<Organization> organizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);
        ArgumentCaptor<OrganizationMembership> organizationMembershipArgumentCaptor = ArgumentCaptor.forClass(OrganizationMembership.class);
        User someUser = new User();

        organizationService.createPersonalOrganization(someUser);

        Mockito.verify(organizationRepository).saveAndFlush(organizationArgumentCaptor.capture());
        Mockito.verify(organizationMembershipRepository).saveAndFlush(organizationMembershipArgumentCaptor.capture());

        Organization savedOrganization = organizationArgumentCaptor.getValue();
        OrganizationMembership savedOrganizationMembership = organizationMembershipArgumentCaptor.getValue();

        Assertions.assertEquals(savedOrganizationMembership.getPk().getOrganizationId(), savedOrganization.getId());
        Assertions.assertEquals(savedOrganizationMembership.getPk().getUserId(), someUser.getId());
        Assertions.assertEquals(OrganizationRole.OWNER, savedOrganizationMembership.getRole());
    }
}