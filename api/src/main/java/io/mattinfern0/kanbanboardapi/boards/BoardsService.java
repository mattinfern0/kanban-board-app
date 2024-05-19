package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardSummaryDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardDetailDtoMapper;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardSummaryDtoMapper;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.tasks.TaskStatusService;
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
    private final TaskRepository taskRepository;
    private final BoardSummaryDtoMapper boardSummaryDtoMapper;
    private final BoardDetailDtoMapper boardDetailDtoMapper;


    private final TaskStatusService taskStatusService;

    @Autowired
    public BoardsService(BoardRepository boardRepository, BoardColumnRepository boardColumnRepository, OrganizationRepository organizationRepository, TaskRepository taskRepository, BoardSummaryDtoMapper boardSummaryDtoMapper, BoardDetailDtoMapper boardDetailDtoMapper, TaskStatusService taskStatusService) {
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.organizationRepository = organizationRepository;
        this.taskRepository = taskRepository;
        this.boardSummaryDtoMapper = boardSummaryDtoMapper;
        this.boardDetailDtoMapper = boardDetailDtoMapper;
        this.taskStatusService = taskStatusService;
    }

    BoardDetailDto getBoardDetail(UUID boardId) {
        Board boardEntity = boardRepository
            .findById(boardId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Board with id %s not found", boardId)));

        return boardDetailDtoMapper.boardToBoardDetailDto(boardEntity);
    }

    List<BoardSummaryDto> getBoardList() {
        List<Board> boardEntities = boardRepository.findAll();
        return boardSummaryDtoMapper.boardsToBoardSummaryDtos(boardEntities);
    }

    @Transactional
    BoardDetailDto createNewBoard(@Valid CreateBoardDto createBoardDtoOld) {
        Organization organization = organizationRepository
            .findById(createBoardDtoOld.organizationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Organization with id %s not found", createBoardDtoOld.organizationId()
                ))
            );

        Board newBoard = new Board();
        newBoard.setOrganization(organization);
        newBoard.setTitle(createBoardDtoOld.title());
        boardRepository.save(newBoard);

        List<BoardColumn> newColumns = createDefaultNewBoardColumns();
        for (BoardColumn column : newColumns) {
            column.setBoard(newBoard);
            boardColumnRepository.save(column);
        }

        return boardDetailDtoMapper.boardToBoardDetailDto(newBoard);
    }

    BoardDetailDto updateBoard(UUID boardId, UpdateBoardHeaderDTO dto) {
        Board board = boardRepository
            .findById(boardId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Board with id %s not found", boardId)));

        board.setTitle(dto.title());
        boardRepository.save(board);
        return boardDetailDtoMapper.boardToBoardDetailDto(board);
    }

    @Transactional
    void deleteBoard(UUID boardId, Boolean deleteTasks) {
        List<Task> tasks = taskRepository.findByBoardId(boardId);
        tasks.forEach(task -> {
            task.setBoardColumn(null);
            task.setBoardColumnOrder(null);
        });
        taskRepository.saveAllAndFlush(tasks);

        List<BoardColumn> columns = boardColumnRepository.findByBoardId(boardId);
        boardColumnRepository.deleteAll(columns);

        boardRepository.deleteById(boardId);

        if (deleteTasks) {

            taskRepository.deleteAll(tasks);
        }
    }

    List<BoardColumn> createDefaultNewBoardColumns() {
        List<BoardColumn> result = new ArrayList<>();
        BoardColumn backlogColumn = new BoardColumn();
        backlogColumn.setTitle("Back Log");
        backlogColumn.setDisplayOrder(1);
        backlogColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.BACKLOG));


        result.add(backlogColumn);

        BoardColumn todoColumn = new BoardColumn();
        todoColumn.setTitle("Todo");
        todoColumn.setDisplayOrder(2);
        todoColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.TODO));
        result.add(todoColumn);

        BoardColumn inProgressColumn = new BoardColumn();
        inProgressColumn.setTitle("In-Progress");
        inProgressColumn.setDisplayOrder(3);
        inProgressColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.IN_PROGRESS));
        result.add(inProgressColumn);

        BoardColumn doneColumn = new BoardColumn();
        doneColumn.setTitle("Done");
        doneColumn.setDisplayOrder(4);
        doneColumn.setTaskStatus(taskStatusService.findOrCreate(TaskStatusCode.COMPLETED));
        result.add(doneColumn);

        return result;
    }
}
