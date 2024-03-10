package io.mattinfern0.kanbanboardapi.tasks.validators;

import io.mattinfern0.kanbanboardapi.core.constraints.TaskColumnAndStatusComboValid;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateTaskDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateUpdateTaskColumnStatusValidator implements ConstraintValidator<TaskColumnAndStatusComboValid, CreateTaskDto> {
    private final BoardColumnRepository boardColumnRepository;

    @Autowired
    public CreateUpdateTaskColumnStatusValidator(BoardColumnRepository boardColumnRepository) {
        this.boardColumnRepository = boardColumnRepository;
    }

    @Override
    public void initialize(TaskColumnAndStatusComboValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateTaskDto createTaskDto, ConstraintValidatorContext constraintValidatorContext) {
        if (createTaskDto == null) {
            return true;
        }

        if (createTaskDto.getBoardColumnId() == null || createTaskDto.getStatus() == null) {
            return false;
        }

        Optional<BoardColumn> boardColumn = boardColumnRepository.findById(createTaskDto.getBoardColumnId());

        if (boardColumn.isEmpty()) {
            // Don't throw error if board column does not exist
            return true;
        }

        return boardColumn.get().getTaskStatus().getCodename().equals(createTaskDto.getStatus());
    }
}
