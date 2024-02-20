package io.mattinfern0.kanbanboardapi.core.mappers;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BoardSummaryDtoMapper {
    @Mapping(target = "organizationId", source = "board.organization.id")
    BoardSummaryDto boardToBoardSummaryDto(Board board);

    List<BoardSummaryDto> boardsToBoardSummaryDtos(List<Board> boards);
}
