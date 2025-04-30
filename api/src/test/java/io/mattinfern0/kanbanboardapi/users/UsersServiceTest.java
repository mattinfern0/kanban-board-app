package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.organizations.OrganizationService;
import io.mattinfern0.kanbanboardapi.users.dtos.SignUpDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThrows;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @InjectMocks
    UsersService usersService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationService organizationService;

    @Spy
    private UserDTOMapper userDTOMapper = Mappers.getMapper(UserDTOMapper.class);

    @Test
    void signUpUser_works_correctly() {
        String firebaseId = UUID.randomUUID().toString();
        SignUpDto signUpDto = new SignUpDto("John", "Doe");

        Mockito.when(userRepository.existsByFirebaseId(firebaseId)).thenReturn(false);
        usersService.signUpUser(firebaseId, signUpDto);

        Mockito.verify(userRepository).saveAndFlush(Mockito.any());
    }



    @Test
    void signUpUser_throws_error_if_user_with_firebase_id_already_exists() {
        String firebaseId = UUID.randomUUID().toString();
        SignUpDto signUpDto = new SignUpDto("John", "Doe");

        Mockito.when(userRepository.existsByFirebaseId(firebaseId)).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> usersService.signUpUser(firebaseId, signUpDto));

        assert exception.getMessage().equals("User already exists");
    }

    @Test
    void signUpUser_creates_personal_organization() {
        String firebaseId = UUID.randomUUID().toString();
        SignUpDto signUpDto = new SignUpDto("John", "Doe");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.when(userRepository.existsByFirebaseId(firebaseId)).thenReturn(false);
        usersService.signUpUser(firebaseId, signUpDto);

        Mockito.verify(userRepository).saveAndFlush(userCaptor.capture());
        Mockito.verify(organizationService).createPersonalOrganization(userCaptor.capture());

        List<User> capturedUsers = userCaptor.getAllValues();

        User savedUser = capturedUsers.getFirst();
        User passedUser = capturedUsers.get(1);

        Assertions.assertEquals(savedUser.getId(), passedUser.getId());
    }
}