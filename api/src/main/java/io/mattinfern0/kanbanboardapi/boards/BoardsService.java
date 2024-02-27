package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardDetailDtoMapper;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardSummaryDtoMapper;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BoardsService {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final OrganizationRepository organizationRepository;
    private final BoardSummaryDtoMapper boardSummaryDtoMapper;
    private final BoardDetailDtoMapper boardDetailDtoMapper;


    @Autowired
    public BoardsService(BoardRepository boardRepository, BoardColumnRepository boardColumnRepository, OrganizationRepository organizationRepository, BoardSummaryDtoMapper boardSummaryDtoMapper, BoardDetailDtoMapper boardDetailDtoMapper) {
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.organizationRepository = organizationRepository;
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

    @Transactional
    BoardDetailDto createNewBoard(@Valid CreateBoardDto createBoardDto) {
        Organization organization = organizationRepository
            .findById(createBoardDto.getOrganizationId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Organization with id %s not found", createBoardDto.getOrganizationId()
                ))
            );

        Board newBoard = new Board();
        newBoard.setOrganization(organization);
        newBoard.setTitle(createBoardDto.getTitle());
        boardRepository.save(newBoard);

        List<BoardColumn> newColumns = createDefaultNewBoardColumns();
        for (BoardColumn column: newColumns) {
            column.setBoard(newBoard);
            boardColumnRepository.save(column);
        }

        return boardDetailDtoMapper.boardToBoardDetailDto(newBoard);
    }

    protected List<BoardColumn> createDefaultNewBoardColumns() {
        List<BoardColumn> result = new ArrayList<>();
        BoardColumn backlogColumn = new BoardColumn();
        backlogColumn.setTitle("Back Log");
        backlogColumn.setDisplayOrder(1);
        result.add(backlogColumn);

        BoardColumn todoColumn = new BoardColumn();
        todoColumn.setTitle("Todo");
        todoColumn.setDisplayOrder(2);
        result.add(todoColumn);

        BoardColumn inProgressColumn = new BoardColumn();
        inProgressColumn.setTitle("In-Progress");
        inProgressColumn.setDisplayOrder(3);
        result.add(inProgressColumn);

        BoardColumn doneColumn = new BoardColumn();
        doneColumn.setTitle("Done");
        doneColumn.setDisplayOrder(4);
        result.add(doneColumn);

        return result;
    }
}
