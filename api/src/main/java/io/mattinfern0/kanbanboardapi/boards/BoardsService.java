package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardDetailDtoMapper;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardSummaryDtoMapper;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BoardsService {

    private final BoardRepository boardRepository;
    private final BoardSummaryDtoMapper boardSummaryDtoMapper;
    private final BoardDetailDtoMapper boardDetailDtoMapper;

    @Autowired
    public BoardsService(BoardRepository boardRepository, BoardSummaryDtoMapper boardSummaryDtoMapper, BoardDetailDtoMapper boardDetailDtoMapper) {
        this.boardRepository = boardRepository;
        this.boardSummaryDtoMapper = boardSummaryDtoMapper;
        this.boardDetailDtoMapper = boardDetailDtoMapper;
    }

    BoardDetailDto getBoardDetail(UUID boardId) {
        Board boardEntity = boardRepository
            .findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Board with id %s not found", boardId)));

        return boardDetailDtoMapper.boardToBoardDetailDto(boardEntity);
    }

    List<BoardSummaryDto> getBoardList() {
        List<Board> boardEntities = boardRepository.findAll();
        return boardSummaryDtoMapper.boardsToBoardSummaryDtos(boardEntities);
    }
}
