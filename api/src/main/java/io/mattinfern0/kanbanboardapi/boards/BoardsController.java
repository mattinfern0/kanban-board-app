package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardsController {
    private final BoardsService boardsService;

    @Autowired
    public BoardsController(BoardsService boardsService) {
        this.boardsService = boardsService;
    }

    @GetMapping("")
    List<BoardSummaryDto> listBoards(
    ) {
        return boardsService.getBoardList();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    BoardDetailDto createBoard(@RequestBody @Valid CreateBoardDto createBoardDtoOld) {
        return boardsService.createNewBoard(createBoardDtoOld);
    }

    @PutMapping("/{boardId}/header")
    BoardDetailDto updateBoardHeader(@PathVariable UUID boardId, @RequestBody @Valid UpdateBoardHeaderDTO updateBoardHeaderDTO) {
        return boardsService.updateBoard(boardId, updateBoardHeaderDTO);
    }

    @GetMapping("/{boardId}")
    BoardDetailDto getBoard(@PathVariable UUID boardId) {
        return boardsService.getBoardDetail(boardId);
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBoard(@PathVariable UUID boardId, boolean deleteTasks) {
        boardsService.deleteBoard(boardId, deleteTasks);
    }
}
