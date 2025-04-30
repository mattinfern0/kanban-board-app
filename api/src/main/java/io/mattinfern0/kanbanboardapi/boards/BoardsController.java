package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardsController {
    private final BoardsService boardsService;
    private final UserAccessService userAccessService;

    @Autowired
    public BoardsController(BoardsService boardsService, UserAccessService userAccessService) {
        this.boardsService = boardsService;
        this.userAccessService = userAccessService;
    }

    @GetMapping("")
    List<BoardSummaryDto> listBoards(
        @RequestParam(required = false) UUID organizationId,
        Principal principal
    ) {
        if (organizationId == null) {
            return List.of();
        }

        if (!userAccessService.canAccessOrganization(principal.getName(), organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this resource");
        }

        return boardsService.getBoardList(organizationId);
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
