package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.core.dtos.BoardDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardsService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardsService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    BoardDto getBoardDetail(UUID boardId) {
        Board boardEntity = boardRepository
            .findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Board with id %s not found", boardId)));

        return new BoardDto();
    }
}
