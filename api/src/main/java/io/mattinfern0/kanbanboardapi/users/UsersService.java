package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.users.dtos.SignUpDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserPrivateDetailDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserSummaryDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {
    final UserRepository userRepository;
    final UserDTOMapper userDTOMapper;
    final OrganizationRepository organizationRepository;

    public UsersService(UserRepository userRepository, UserDTOMapper userDTOMapper, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.organizationRepository = organizationRepository;
    }

    List<UserSummaryDto> getUserList() {
        List<User> entities = userRepository.findAll();
        return userDTOMapper.entityListtoSummaryDtoList(entities);
    }

    @Transactional
    UserSummaryDto signUpUser(String firebaseId, SignUpDto signUpDto) {
        if (userRepository.existsByFirebaseId(firebaseId)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setFirebaseId(firebaseId);
        user.setFirstName(signUpDto.firstName());
        user.setLastName(signUpDto.lastName());
        userRepository.save(user);

        Organization personalOrganization = new Organization();
        personalOrganization.setPersonalForUser(user);
        personalOrganization.setDisplayName(String.format("Personal - User %s", user.getId()));
        organizationRepository.save(personalOrganization);

        return userDTOMapper.entityToSummaryDto(user);
    }

    UserPrivateDetailDto getUserByFirebaseId(String firebaseId) {
        User user = userRepository.findByFirebaseId(firebaseId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userDTOMapper.entityToPrivateDetailDto(user);
    }
}
