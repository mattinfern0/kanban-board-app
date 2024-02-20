package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.core.dtos.BoardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardsController {
    private final BoardsService boardsService;

    @Autowired
    public BoardsController(BoardsService boardsService) {
        this.boardsService = boardsService;
    }

    @GetMapping("/{boardId}")
    BoardDto getBoard(@PathVariable UUID boardId) {
        return boardsService.getBoardDetail(boardId);
    }
}
