package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.users.dtos.UserPrivateDetailDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserDTOMapper {
    UserSummaryDto entityToSummaryDto(User user);

    List<UserSummaryDto> entityListtoSummaryDtoList(List<User> users);

    @Mapping(target = "personalOrganizationId", source = "personalOrganization.id")
    UserPrivateDetailDto entityToPrivateDetailDto(User entity);
}
