package io.mattinfern0.kanbanboardapi.organizations;

import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.config.FirebaseTestConfig;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import io.mattinfern0.kanbanboardapi.organizations.mappers.InviteDtoMapper;
import io.mattinfern0.kanbanboardapi.users.FirebaseUserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.security.Principal;
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
    OrganizationInviteRepository inviteRepository;

    @Mock
    FirebaseUserService firebaseUserService;

    @Spy
    InviteDtoMapper inviteDtoMapper = Mappers.getMapper(InviteDtoMapper.class);

    @InjectMocks
    InviteService inviteService;

    @Nested
    class CreateInviteTests {
        @Test
        void createInvite() {

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

            InviteDto result = inviteService.createInvite(createInviteDto);

            Assertions.assertAll(
                () -> Assertions.assertEquals(createInviteDto.email(), result.email()),
                () -> Assertions.assertEquals(testOrganization.getId(), result.organization().id()),
                () -> Assertions.assertEquals(testOrganization.getDisplayName(), result.organization().name()),
                () -> Assertions.assertEquals(OrganizationInviteStatus.PENDING, result.status())
            );
        }
    }

    @Nested
    class AcceptInviteTests {
        @Test
        void throwsError_ifInviteIsRevoked() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.REVOKED);
            invite.setEmail("testUser@email");

            Mockito.when(inviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                IllegalStateException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsAlreadyAccepted() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.ACCEPTED);
            invite.setEmail("testUser@email");

            Mockito.when(inviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                IllegalStateException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        @Disabled("TODO")
        void throwsError_ifInviteIsExpired() {
            ZonedDateTime testExpiresAt = ZonedDateTime.of(
                2023, 10, 1, 1, 0, 0, 0,
                ZoneOffset.UTC
            );

            ZonedDateTime testCurrentTime = ZonedDateTime.of(
                2023, 10, 2, 1, 0, 0, 0,
                ZoneOffset.UTC
            );

            Mockito.when(ZonedDateTime.now()).thenReturn(testCurrentTime);

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setExpiresAt(testExpiresAt);
            invite.setEmail("testUser@email");

            Mockito.when(inviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

            Exception ex = assertThrows(
                IllegalStateException.class,
                () -> inviteService.acceptInvite(principal, "some_token")
            );

            Assertions.assertEquals("Invite is expired", ex.getMessage());
        }

        @Test
        void setsStatusToAccepted() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.PENDING);
            invite.setEmail("testUser@email");

            Mockito.when(inviteRepository
                    .findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(invite));

            Principal principal = Mockito.mock(Principal.class);
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            Mockito.when(mockUserRecord.getEmail()).thenReturn(invite.getEmail());

            Mockito.when(firebaseUserService
                    .getUserDetails(principal))
                .thenReturn(mockUserRecord);

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

            Mockito.when(inviteRepository
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

            Mockito.when(inviteRepository
                    .findById(invite.getId()))
                .thenReturn(Optional.of(invite));

            Exception ex = assertThrows(
                IllegalStateException.class,
                () -> inviteService.revokeInvite(invite.getId())
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }

        @Test
        void throwsError_ifInviteIsAlreadyRevoked() {

            OrganizationInvite invite = new OrganizationInvite();
            invite.setStatus(OrganizationInviteStatus.REVOKED);
            invite.setId(UUID.randomUUID());

            Mockito.when(inviteRepository
                    .findById(invite.getId()))
                .thenReturn(Optional.of(invite));

            Exception ex = assertThrows(
                IllegalStateException.class,
                () -> inviteService.revokeInvite(invite.getId())
            );

            Assertions.assertEquals("Invite is not pending", ex.getMessage());
        }
    }
}