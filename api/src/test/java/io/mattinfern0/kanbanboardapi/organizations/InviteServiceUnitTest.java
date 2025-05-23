package io.mattinfern0.kanbanboardapi.organizations;

import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.config.FirebaseTestConfig;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import io.mattinfern0.kanbanboardapi.core.exceptions.IllegalOperationException;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import io.mattinfern0.kanbanboardapi.users.FirebaseUserService;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
@Import({FirebaseTestConfig.class})
@ExtendWith(MockitoExtension.class)
class InviteServiceUnitTest {
    @Mock
    OrganizationRepository organizationRepository;

    @Mock
    OrganizationMembershipRepository organizationMembershipRepository;

    @Mock
    OrganizationInviteRepository organiztionInviteRepository;

    @Mock
    FirebaseUserService firebaseUserService;

    @Mock
    UserAccessService userAccessService;

    @Mock
    OrganizationService organizationService;

    @Mock
    Clock clock;

    @Spy
    InviteDtoMapper inviteDtoMapper = Mappers.getMapper(InviteDtoMapper.class);

    @InjectMocks
    InviteService inviteService;

    @Nested
    class CreateInviteTests {
        @Test
        void createsCorrectInvite() {

            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            testOrganization.setDisplayName("Test Organization");
            CreateInviteDto createInviteDto = new CreateInviteDto(
                testOrganization.getId(),
                "jSmith42@email.com"
            );

            Mockito.when(organizationRepository
                    .findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            Principal mockPrincipal = Mockito.mock(Principal.class);
            OrganizationMembership mockMembership = new OrganizationMembership();
            mockMembership.setRole(OrganizationRole.OWNER);
            Mockito.when(userAccessService
                    .getMembership(mockPrincipal, testOrganization.getId()))
                .thenReturn(Optional.of(mockMembership));

            InviteDetailDto result = inviteService.createInvite(mockPrincipal, createInviteDto);

            Assertions.assertAll(
                () -> Assertions.assertTrue(createInviteDto.email().equalsIgnoreCase(result.email())),
                () -> Assertions.assertEquals(testOrganization.getId(), result.organization().id()),
                () -> Assertions.assertEquals(testOrganization.getDisplayName(), result.organization().name()),
                () -> Assertions.assertEquals(OrganizationInviteStatus.PENDING, result.status()),
                () -> Assertions.assertNotNull(result.token()),
                () -> Assertions.assertNotNull(result.expiresAt())
            );
        }

        @Test
        void throwsError_ifPrincipalIsNotOwnerOfOrganization() {

            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            testOrganization.setDisplayName("Test Organization");
            CreateInviteDto createInviteDto = new CreateInviteDto(
                testOrganization.getId(),
                "jSmith42@email.com"
            );

            Mockito.when(organizationRepository
                    .findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            Principal mockPrincipal = Mockito.mock(Principal.class);
            OrganizationMembership mockMembership = new OrganizationMembership();
            mockMembership.setRole(OrganizationRole.MEMBER);
            Mockito.when(userAccessService
                    .getMembership(mockPrincipal, testOrganization.getId()))
                .thenReturn(Optional.of(mockMembership));

            Exception ex = Assertions.assertThrows(AccessDeniedException.class, () -> inviteService.createInvite(mockPrincipal, createInviteDto));

            Assertions.assertEquals("You do not have permission to invite users to this organization", ex.getMessage());
        }

        @Test
        void throwsError_ifPrincipalIsNotPartOfOrganization() {

            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            testOrganization.setDisplayName("Test Organization");
            CreateInviteDto createInviteDto = new CreateInviteDto(
                testOrganization.getId(),
                "jSmith42@email.com"
            );

            Mockito.when(organizationRepository
                    .findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            Principal mockPrincipal = Mockito.mock(Principal.class);

            Exception ex = Assertions.assertThrows(AccessDeniedException.class, () -> inviteService.createInvite(mockPrincipal, createInviteDto));

            Assertions.assertEquals("You do not have permission to invite users to this organization", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteeIsAlreadyPartOfOrganization() {
            String inviteeEmail = "jSmith42@email.com";

            Organization organization = new Organization();
            organization.setId(UUID.randomUUID());
            organization.setDisplayName("Test Organization");
            CreateInviteDto createInviteDto = new CreateInviteDto(
                organization.getId(),
                inviteeEmail
            );

            Mockito.when(organizationRepository
                    .findById(organization.getId()))
                .thenReturn(Optional.of(organization));

            Principal principal = Mockito.mock(Principal.class);
            OrganizationMembership prinicipalMembership = new OrganizationMembership();
            prinicipalMembership.setRole(OrganizationRole.OWNER);
            Mockito.when(userAccessService
                    .getMembership(principal, organization.getId()))
                .thenReturn(Optional.of(prinicipalMembership));


            User inviteeUser = new User();
            inviteeUser.setFirebaseId("firebaseId123");
            inviteeUser.setId(UUID.randomUUID());

            Mockito.when(firebaseUserService.getUserEntityByEmail(inviteeEmail))
                .thenReturn(Optional.of(inviteeUser));

            OrganizationMembership inviteeMembership = new OrganizationMembership(organization, inviteeUser);
            inviteeMembership.setRole(OrganizationRole.MEMBER);

            Mockito.when(organizationMembershipRepository.existsByUserAndOrganization(inviteeUser, organization))
                .thenReturn(true);

            Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () -> inviteService.createInvite(principal, createInviteDto));

            Assertions.assertEquals("Invitee is already a member of the organization", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteeAlreadyHasActiveInvite() {
            String inviteeEmail = "jSmith42@email.com";

            Organization organization = new Organization();
            organization.setId(UUID.randomUUID());
            organization.setDisplayName("Test Organization");
            CreateInviteDto createInviteDto = new CreateInviteDto(
                organization.getId(),
                inviteeEmail
            );

            Mockito.when(organizationRepository
                    .findById(organization.getId()))
                .thenReturn(Optional.of(organization));

            Principal principal = Mockito.mock(Principal.class);
            OrganizationMembership prinicipalMembership = new OrganizationMembership();
            prinicipalMembership.setRole(OrganizationRole.OWNER);
            Mockito.when(userAccessService
                    .getMembership(principal, organization.getId()))
                .thenReturn(Optional.of(prinicipalMembership));

            OrganizationInvite expectedInvite = new OrganizationInvite();
            expectedInvite.setEmail(inviteeEmail);
            expectedInvite.setOrganization(organization);
            expectedInvite.setStatus(OrganizationInviteStatus.PENDING);
            expectedInvite.setToken(UUID.randomUUID().toString());
            expectedInvite.setExpiresAt(ZonedDateTime.now().plusDays(99));

            Mockito.when(organiztionInviteRepository.findActiveInviteByEmailAndOrganizationId(inviteeEmail, organization.getId()))
                .thenReturn(Optional.of(expectedInvite));

            Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () -> inviteService.createInvite(principal, createInviteDto));

            Assertions.assertEquals("Invitee has already been invited to the organization", ex.getMessage());
        }
    }

    @Nested
    class AcceptInviteTests {
        @Test
        void marks_invite_as_accepted() {
            String userEmail = "appleSauce@email.com";
            String inviteToken = "some_token";
            ZonedDateTime expiresAt = ZonedDateTime.of(2022, 3, 14, 12, 0, 0, 0, ZoneOffset.UTC);


            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setEmail(userEmail);
            invite.setToken(inviteToken);
            invite.setOrganization(testOrganization);
            invite.setExpiresAt(expiresAt);

            Mockito.when(organiztionInviteRepository
                    .findByToken(inviteToken))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(userEmail);

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Mockito.when(clock.instant())
                .thenReturn(expiresAt.minusNanos(1).toInstant());
            Mockito.when(clock.getZone())
                .thenReturn(ZoneOffset.UTC);

            ArgumentCaptor<OrganizationInvite> inviteArgumentCaptor = ArgumentCaptor.forClass(OrganizationInvite.class);
            inviteService.acceptInvite(principal, inviteToken);

            Mockito.verify(organiztionInviteRepository).save(inviteArgumentCaptor.capture());

            OrganizationInvite savedInvite = inviteArgumentCaptor.getValue();

            Assertions.assertEquals(OrganizationInviteStatus.ACCEPTED, savedInvite.getStatus());
        }

        @Test
        void adds_user_as_a_member() {
            String userEmail = "appleSauce@email.com";
            String inviteToken = "some_token";
            ZonedDateTime expiresAt = ZonedDateTime.of(2022, 3, 14, 12, 0, 0, 0, ZoneOffset.UTC);

            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setEmail(userEmail);
            invite.setToken(inviteToken);
            invite.setOrganization(testOrganization);
            invite.setExpiresAt(expiresAt);

            Mockito.when(organiztionInviteRepository
                    .findByToken(inviteToken))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(userEmail);

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Mockito.when(clock.instant())
                .thenReturn(expiresAt.minusNanos(1).toInstant());
            Mockito.when(clock.getZone())
                .thenReturn(ZoneOffset.UTC);

            inviteService.acceptInvite(principal, inviteToken);

            Mockito.verify(organizationService).addUserToOrganization(principal, testOrganization.getId(), OrganizationRole.MEMBER);

        }

        @Test
        void throwsError_ifUsersEmailDoesNotMatchInvite() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.ACCEPTED);
            invite.setEmail("appleSauce@email");

            Mockito.when(organiztionInviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn("orangeJuice@email");

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                AccessDeniedException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("This invite is not for you", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsRevoked() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.REVOKED);
            invite.setEmail("testUser@email");

            Mockito.when(organiztionInviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                IllegalOperationException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsAlreadyAccepted() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.ACCEPTED);
            invite.setEmail("testUser@email");

            Mockito.when(organiztionInviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                IllegalOperationException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsExpired() {
            String userEmail = "appleSauce@email.com";
            String inviteToken = "some_token";
            ZonedDateTime expiresAt = ZonedDateTime.of(2022, 3, 14, 12, 1, 0, 0, ZoneOffset.UTC);

            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setEmail(userEmail);
            invite.setToken(inviteToken);
            invite.setOrganization(testOrganization);
            invite.setExpiresAt(expiresAt);

            Mockito.when(organiztionInviteRepository
                    .findByToken(inviteToken))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(userEmail);

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Mockito.when(clock.instant())
                .thenReturn(expiresAt.plusNanos(1).toInstant());
            Mockito.when(clock.getZone())
                .thenReturn(ZoneOffset.UTC);

            Exception ex = assertThrows(
                IllegalOperationException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite has expired", ex.getMessage());
        }

        @Test
        void setsStatusToAccepted() {
            ZonedDateTime expiresAt = ZonedDateTime.of(2022, 3, 14, 12, 0, 0, 0, ZoneOffset.UTC);

            OrganizationInvite invite = new OrganizationInvite();
            Organization testOrganization = new Organization();
            testOrganization.setId(UUID.randomUUID());
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setEmail("testUser@email");
            invite.setOrganization(testOrganization);
            invite.setExpiresAt(expiresAt);

            Mockito.when(organiztionInviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Mockito.when(clock.instant())
                .thenReturn(expiresAt.minusNanos(1).toInstant());
            Mockito.when(clock.getZone())
                .thenReturn(ZoneOffset.UTC);

            inviteService.acceptInvite(principal, "some_token");

            Assertions.assertEquals(OrganizationInviteStatus.ACCEPTED, invite.getStatus());
        }
    }

    @Nested
    class RevokeInviteTests {
        @Test
        void setsStatusToRevoked() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setId(UUID.randomUUID());

            Mockito.when(organiztionInviteRepository
                    .findById(invite.getId()))
                .thenReturn(Optional.of(invite));

            inviteService.revokeInvite(invite.getId());

            Assertions.assertEquals(OrganizationInviteStatus.REVOKED, invite.getStatus());
        }

        @Test
        void throwsError_ifInviteIsAlreadyAccepted() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.ACCEPTED);
            invite.setId(UUID.randomUUID());

            Mockito.when(organiztionInviteRepository
                    .findById(invite.getId()))
                .thenReturn(Optional.of(invite));

            Exception ex = assertThrows(
                IllegalOperationException.class,
                () -> inviteService.revokeInvite(invite.getId())
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsAlreadyRevoked() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.REVOKED);
            invite.setId(UUID.randomUUID());

            Mockito.when(organiztionInviteRepository
                    .findById(invite.getId()))
                .thenReturn(Optional.of(invite));

            Exception ex = assertThrows(
                IllegalOperationException.class,
                () -> inviteService.revokeInvite(invite.getId())
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }
    }
}