package io.mattinfern0.kanbanboardapi.tasks.validators;

import io.mattinfern0.kanbanboardapi.core.constraints.TaskColumnAndStatusComboValid;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.tasks.dtos.CreateUpdateTaskDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateUpdateTaskColumnStatusValidator implements ConstraintValidator<TaskColumnAndStatusComboValid, CreateUpdateTaskDto> {
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
    public boolean isValid(CreateUpdateTaskDto createUpdateTaskDto, ConstraintValidatorContext constraintValidatorContext) {
        if (createUpdateTaskDto == null) {
            return true;
        }

        if (createUpdateTaskDto.getBoardColumnId() == null || createUpdateTaskDto.getStatus() == null) {
            return true;
        }

        Optional<BoardColumn> boardColumn = boardColumnRepository.findById(createUpdateTaskDto.getBoardColumnId());

        if (boardColumn.isEmpty()) {
            // Don't throw error if board column does not exist
            return true;
        }

        return boardColumn.get().getTaskStatus().getCodename().equals(createUpdateTaskDto.getStatus());
    }
}
