package io.mattinfern0.kanbanboardapi.tasks.mappers;

import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskAssigneeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskAssigneeDtoMapper {
    @Mapping(target = "userId", source = "id")
    TaskAssigneeDto userToTaskAssigneeDto(User user);
}
