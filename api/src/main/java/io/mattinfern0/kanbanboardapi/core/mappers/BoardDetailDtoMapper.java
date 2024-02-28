package io.mattinfern0.kanbanboardapi.core.mappers;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardTaskDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BoardDetailDtoMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    BoardDetailDto boardToBoardDetailDto(Board board);

    @Mapping(target = "taskStatus", source = "taskStatus.codename")
    BoardColumnDto boardColumnToBoardColumnDto(BoardColumn column);

    @Mapping(target = "status", source = "taskStatus.codename")
    BoardTaskDto taskToTaskDto(Task task);
}
