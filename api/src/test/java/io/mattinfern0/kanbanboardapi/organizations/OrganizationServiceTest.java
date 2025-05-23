package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.IllegalOperationException;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationMemberDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.UpdateMembershipDto;
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

    @Nested
    class UpdateMembershipTests {
        @Test
        void correctly_updates_membership() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User ownerUser = new User();
            ownerUser.setId(UUID.randomUUID());
            OrganizationMembership ownerMembership = new OrganizationMembership(testOrganization, ownerUser);
            ownerMembership.setRole(OrganizationRole.OWNER);
            testOrganization.setMemberships(List.of(ownerMembership));

            UpdateMembershipDto testDto = new UpdateMembershipDto(
                OrganizationRole.MEMBER
            );

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.countMembersWithRoleInOrganization(testOrganizationId, OrganizationRole.OWNER))
                .thenReturn(2);


            ArgumentCaptor<OrganizationMembership> memberArgumentCaptor = ArgumentCaptor.forClass(OrganizationMembership.class);
            organizationService.updateMembership(testPrincipal, testOrganizationId, ownerUser.getId(), testDto);

            Mockito.verify(organizationMembershipRepository).saveAndFlush(memberArgumentCaptor.capture());
            OrganizationMembership updatedMembership = memberArgumentCaptor.getValue();
            Assertions.assertEquals(OrganizationRole.MEMBER, updatedMembership.getRole());

        }

        @Test
        void throws_error_organization_wont_have_any_owners_after_update() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User ownerUser = new User();
            ownerUser.setId(UUID.randomUUID());
            OrganizationMembership ownerMembership = new OrganizationMembership(testOrganization, ownerUser);
            ownerMembership.setRole(OrganizationRole.OWNER);
            testOrganization.setMemberships(List.of(ownerMembership));

            UpdateMembershipDto testDto = new UpdateMembershipDto(
                OrganizationRole.MEMBER
            );

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.countMembersWithRoleInOrganization(testOrganizationId, OrganizationRole.OWNER))
                .thenReturn(1);

            Exception ex = Assertions.assertThrows(
                IllegalOperationException.class,
                () -> organizationService.updateMembership(testPrincipal, testOrganizationId, ownerUser.getId(), testDto)
            );

            Assertions.assertEquals(
                "At least 1 member must be an owner in the organization",
                ex.getMessage()
            );
        }

        @Test
        void throws_error_if_principal_is_not_an_owner_of_the_organization() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User principalUser = new User();
            principalUser.setId(UUID.randomUUID());
            OrganizationMembership principalMembership = new OrganizationMembership(testOrganization, principalUser);
            principalMembership.setRole(OrganizationRole.MEMBER);
            testOrganization.setMemberships(List.of(principalMembership));

            UpdateMembershipDto testDto = new UpdateMembershipDto(
                OrganizationRole.MEMBER
            );

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(principalMembership));


            Exception ex = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> organizationService.updateMembership(testPrincipal, testOrganizationId, principalUser.getId(), testDto)
            );

            Assertions.assertEquals(
                "You do not have permission to update memberships in this organization",
                ex.getMessage()
            );
        }

        @Test
        void throws_error_if_principal_is_not_part_of_the_organization() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User principalUser = new User();
            principalUser.setId(UUID.randomUUID());
            OrganizationMembership principalMembership = new OrganizationMembership(testOrganization, principalUser);
            principalMembership.setRole(OrganizationRole.MEMBER);
            testOrganization.setMemberships(List.of(principalMembership));

            UpdateMembershipDto testDto = new UpdateMembershipDto(
                OrganizationRole.MEMBER
            );

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.empty());


            Exception ex = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> organizationService.updateMembership(testPrincipal, testOrganizationId, principalUser.getId(), testDto)
            );

            Assertions.assertEquals(
                "You do not have access to this organization",
                ex.getMessage()
            );
        }
    }

    @Nested
    class DeleteMembershipTests {
        @Test
        void owners_can_delete_any_membership() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);

            User targetUser = new User();
            targetUser.setId(UUID.randomUUID());
            OrganizationMembership targetMembership = new OrganizationMembership(testOrganization, targetUser);
            targetMembership.setRole(OrganizationRole.MEMBER);

            User ownerUser = new User();
            ownerUser.setId(UUID.randomUUID());
            OrganizationMembership ownerMembership = new OrganizationMembership(testOrganization, ownerUser);
            ownerMembership.setRole(OrganizationRole.OWNER);

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(targetMembership));

            ArgumentCaptor<OrganizationMembership> memberArgumentCaptor = ArgumentCaptor.forClass(OrganizationMembership.class);
            organizationService.deleteMembership(testPrincipal, testOrganizationId, ownerUser.getId());

            Mockito.verify(organizationMembershipRepository).delete(memberArgumentCaptor.capture());
            OrganizationMembership deletedMembership = memberArgumentCaptor.getValue();
            Assertions.assertEquals(targetMembership, deletedMembership);
        }

        @Test
        void users_can_delete_their_own_membership() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);

            User principalUser = new User();
            principalUser.setId(UUID.randomUUID());
            OrganizationMembership principalMembership = new OrganizationMembership(testOrganization, principalUser);

            // Should be able to delete their own membership, even if they are not an owner
            principalMembership.setRole(OrganizationRole.MEMBER);

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(principalMembership));

            Mockito
                .when(organizationMembershipRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(principalMembership));
            
            ArgumentCaptor<OrganizationMembership> memberArgumentCaptor = ArgumentCaptor.forClass(OrganizationMembership.class);
            organizationService.deleteMembership(testPrincipal, testOrganizationId, principalUser.getId());

            Mockito.verify(organizationMembershipRepository).delete(memberArgumentCaptor.capture());
            OrganizationMembership deletedMembership = memberArgumentCaptor.getValue();
            Assertions.assertEquals(principalMembership, deletedMembership);
        }

        @Test
        void throws_error_organization_wont_have_any_owners_after_delete() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User ownerUser = new User();
            ownerUser.setId(UUID.randomUUID());
            OrganizationMembership ownerMembership = new OrganizationMembership(testOrganization, ownerUser);
            ownerMembership.setRole(OrganizationRole.OWNER);
            testOrganization.setMemberships(List.of(ownerMembership));

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ownerMembership));

            Mockito
                .when(organizationMembershipRepository.countMembersWithRoleInOrganization(testOrganizationId, OrganizationRole.OWNER))
                .thenReturn(1);

            Exception ex = Assertions.assertThrows(
                IllegalOperationException.class,
                () -> organizationService.deleteMembership(testPrincipal, testOrganizationId, ownerUser.getId())
            );

            Assertions.assertEquals(
                "You cannot delete the last owner of the organization",
                ex.getMessage()
            );
        }

        @Test
        void throws_error_if_principal_is_removing_a_different_member_and_is_not_an_owner_of_the_organization() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);

            User principalUser = new User();
            principalUser.setId(UUID.randomUUID());
            OrganizationMembership principalMembership = new OrganizationMembership(testOrganization, principalUser);
            principalMembership.setRole(OrganizationRole.MEMBER);

            User otherUser = new User();
            otherUser.setId(UUID.randomUUID());
            OrganizationMembership targetMembership = new OrganizationMembership(testOrganization, otherUser);
            targetMembership.setRole(OrganizationRole.MEMBER);

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.of(principalMembership));

            Exception ex = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> organizationService.deleteMembership(testPrincipal, testOrganizationId, otherUser.getId())
            );

            Assertions.assertEquals(
                "You do not have permission to delete this membership",
                ex.getMessage()
            );
        }

        @Test
        void throws_error_if_principal_is_not_part_of_the_organization() {
            Principal testPrincipal = Mockito.mock(Principal.class);
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            User principalUser = new User();
            principalUser.setId(UUID.randomUUID());
            OrganizationMembership principalMembership = new OrganizationMembership(testOrganization, principalUser);
            principalMembership.setRole(OrganizationRole.MEMBER);
            testOrganization.setMemberships(List.of(principalMembership));

            Mockito
                .when(userAccessService.getMembership(testPrincipal, testOrganizationId))
                .thenReturn(Optional.empty());


            Exception ex = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> organizationService.deleteMembership(testPrincipal, testOrganizationId, principalUser.getId())
            );

            Assertions.assertEquals(
                "You do not have access to this organization",
                ex.getMessage()
            );
        }
    }
}