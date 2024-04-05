package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskOrderItemDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskReorderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/board-columns")
public class BoardColumnController {
    private final BoardColumnService boardColumnService;

    @Autowired
    public BoardColumnController(BoardColumnService boardColumnService) {
        this.boardColumnService = boardColumnService;
    }

    @PutMapping("/{columnId}/tasks/order")
    public List<BoardColumnTaskOrderItemDto> reorderTasks(
        @PathVariable UUID columnId,
        @RequestBody List<BoardColumnTaskOrderItemDto> newOrder
    ) {
        BoardColumnTaskReorderDto dto = new BoardColumnTaskReorderDto(
            columnId,
            newOrder
        );

        return boardColumnService.reorderTasks(dto);
    }
}
