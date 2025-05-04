package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationMemberDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.OrganizationDtoMapper;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.*;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {
    @InjectMocks
    OrganizationService organizationService;

    @Mock
    OrganizationRepository organizationRepository;

    @Mock
    OrganizationMembershipRepository organizationMembershipRepository;

    @Mock
    UserAccessService userAccessService;

    @Spy
    OrganizationDtoMapper organizationDtoMapper = Mappers.getMapper(OrganizationDtoMapper.class);

    @Nested
    class CreatePersonalOrganizationTests {
        @Test
        void creates_correct_organization_record() {
            ArgumentCaptor<Organization> organizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);
            User someUser = new User();

            organizationService.createPersonalOrganization(someUser);

            Mockito.verify(organizationRepository).saveAndFlush(organizationArgumentCaptor.capture());
            Organization savedOrganization = organizationArgumentCaptor.getValue();
            Assertions.assertEquals(savedOrganization.getPersonalForUser(), someUser);
        }

        @Test
        void creates_correct_membership_record() {
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

    @Nested
    class GetOrganizationDetailsTests {
        @Test
        void throws_exception_if_organization_does_not_exist() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Mockito
                .when(organizationRepository.findById(testOrganizationId))
                .thenReturn(java.util.Optional.empty());
            Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> organizationService.getOrganizationDetails(testPrincipal, testOrganizationId)
            );
        }

        @Test
        void throws_exception_if_user_does_not_have_access_to_organization() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            Mockito
                .when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(false);

            Assertions.assertThrows(
                AccessDeniedException.class,
                () -> organizationService.getOrganizationDetails(testPrincipal, testOrganization.getId())
            );
        }

        @Test
        void returns_expected_data() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            testOrganization.setDisplayName("Test Organization");
            testOrganization.setPersonalForUser(new User());

            Mockito
                .when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));
            Mockito
                .when(userAccessService.canAccessOrganization(testPrincipal, testOrganization.getId()))
                .thenReturn(true);

            User user1 = new User();
            user1.setId(UUID.randomUUID());
            user1.setFirstName("John");
            user1.setLastName("Doe");
            OrganizationMembership membership1 = new OrganizationMembership(testOrganization, user1);
            membership1.setRole(OrganizationRole.OWNER);

            User user2 = new User();
            user2.setId(UUID.randomUUID());
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            OrganizationMembership membership2 = new OrganizationMembership(testOrganization, user2);
            membership2.setRole(OrganizationRole.MEMBER);

            List<OrganizationMembership> membershipEntities = List.of(membership1, membership2);
            testOrganization.setMemberships(membershipEntities);

            OrganizationDetailsDto result = organizationService.getOrganizationDetails(testPrincipal, testOrganization.getId());

            Assertions.assertAll(
                () -> Assertions.assertEquals(testOrganization.getId(), result.id()),
                () -> Assertions.assertEquals(testOrganization.getDisplayName(), result.displayName()),
                () -> Assertions.assertTrue(result.isPersonal()),
                () -> {
                    Set<OrganizationMemberDto> expectedSet = Set.of(
                        new OrganizationMemberDto(
                            user1.getId(),
                            user1.getFirstName(),
                            user1.getLastName(),
                            OrganizationRole.OWNER
                        ),
                        new OrganizationMemberDto(
                            user2.getId(),
                            user2.getFirstName(),
                            user2.getLastName(),
                            OrganizationRole.MEMBER
                        )
                    );
                    Set<OrganizationMemberDto> actualSet = new HashSet<>(result.members());

                    Assertions.assertEquals(expectedSet, actualSet);
                }
            );
        }
    }
}