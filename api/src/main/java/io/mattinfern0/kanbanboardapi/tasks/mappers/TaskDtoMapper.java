package io.mattinfern0.kanbanboardapi.tasks.mappers;

import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.tasks.dtos.TaskDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TaskDtoMapper {
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "boardColumnId", source = "boardColumn.id")
    @Mapping(target = "status", source = "taskStatus.codename")
    TaskDetailDto taskToTaskDetailDto(Task taskEntity);

    List<TaskDetailDto> taskListToTaskDetailDtoList(List<Task> tasks);
}
