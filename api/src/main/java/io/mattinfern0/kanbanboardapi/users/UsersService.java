package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
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
    final OrganizationMembershipRepository organizationMembershipRepository;

    public UsersService(UserRepository userRepository, UserDTOMapper userDTOMapper, OrganizationRepository organizationRepository, OrganizationMembershipRepository organizationMembershipRepository) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.organizationRepository = organizationRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
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
        userRepository.saveAndFlush(user);

        createPersonalOrganization(user);

        return userDTOMapper.entityToSummaryDto(user);
    }

    UserPrivateDetailDto getUserByFirebaseId(String firebaseId) {
        User user = userRepository.findByFirebaseId(firebaseId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userDTOMapper.entityToPrivateDetailDto(user);
    }

    @Transactional
    Organization createPersonalOrganization(User user) {
        Organization personalOrganization = new Organization();
        personalOrganization.setPersonalForUser(user);
        personalOrganization.setDisplayName(String.format("Personal - User %s", user.getId()));
        organizationRepository.saveAndFlush(personalOrganization);

        OrganizationMembership organizationMembership = new OrganizationMembership();
        OrganizationMembershipPk organizationMembershipPk = new OrganizationMembershipPk();
        organizationMembershipPk.setOrganizationId(personalOrganization.getId());
        organizationMembershipPk.setUserId(user.getId());
        organizationMembership.setPk(organizationMembershipPk);

        organizationMembershipRepository.saveAndFlush(organizationMembership);
        return personalOrganization;
    }
}
