package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.users.dtos.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserDTOMapper {
    UserDto entityToDto(User user);

    List<UserDto> entityListtoDtoList(List<User> users);
}
