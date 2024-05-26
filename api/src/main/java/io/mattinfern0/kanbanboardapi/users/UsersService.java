package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import io.mattinfern0.kanbanboardapi.users.dtos.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
