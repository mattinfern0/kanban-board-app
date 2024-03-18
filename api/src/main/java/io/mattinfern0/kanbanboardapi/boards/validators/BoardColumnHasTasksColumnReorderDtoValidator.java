package io.mattinfern0.kanbanboardapi.boards.validators;

import io.mattinfern0.kanbanboardapi.boards.contraints.BoardColumnOrderTasksOrderOnlyHasAllColumnTasks;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskOrderItemDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnTaskReorderDto;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoardColumnHasTasksColumnReorderDtoValidator implements ConstraintValidator<BoardColumnOrderTasksOrderOnlyHasAllColumnTasks, BoardColumnTaskReorderDto> {

    private final BoardColumnRepository boardColumnRepository;

    @Autowired
    public BoardColumnHasTasksColumnReorderDtoValidator(BoardColumnRepository boardColumnRepository) {
        this.boardColumnRepository = boardColumnRepository;
    }

    @Override
    public void initialize(BoardColumnOrderTasksOrderOnlyHasAllColumnTasks constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BoardColumnTaskReorderDto boardColumnTaskReorderDto, ConstraintValidatorContext constraintValidatorContext) {
        Optional<BoardColumn> column = boardColumnRepository.findById(boardColumnTaskReorderDto.getBoardColumnId());
        if (column.isEmpty()) {
            return true;
        }

        Set<UUID> columnTaskIds = column.get().getTasks().stream()
                .map(Task::getId)
                .collect(Collectors.toSet());

        Set<UUID> orderTaskIds = boardColumnTaskReorderDto.getNewOrder().stream()
                .map(BoardColumnTaskOrderItemDto::getTaskId)
                .collect(Collectors.toSet());

        Set<UUID> idDifference = SetUtils.disjunction(columnTaskIds, orderTaskIds);
        return idDifference.isEmpty();
    }
}
