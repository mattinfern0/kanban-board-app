package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.users.dtos.SignUpDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService {
    final UserRepository userRepository;
    final UserDTOMapper userDTOMapper;

    public UsersService(UserRepository userRepository, UserDTOMapper userDTOMapper) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
    }

    List<UserDto> getUserList() {
        List<User> entities = userRepository.findAll();
        return userDTOMapper.entityListtoDtoList(entities);
    }

    @Transactional
    UserDto signUpUser(UUID firebaseId, SignUpDto signUpDto) {
        if (userRepository.existsByFirebaseId(firebaseId)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setFirebaseId(firebaseId);
        user.setFirstName(signUpDto.firstName());
        user.setLastName(signUpDto.lastName());
        userRepository.save(user);
        return userDTOMapper.entityToDto(user);
    }

    UserDto getUserByFirebaseId(UUID firebaseId) {
        User user = userRepository.findByFirebaseId(firebaseId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userDTOMapper.entityToDto(user);
    }
}
