package io.mattinfern0.kanbanboardapi.core.validators;

import io.mattinfern0.kanbanboardapi.core.constraints.TaskColumnAndStatusComboValid;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskEntityColumnAndStatusValidator implements ConstraintValidator<TaskColumnAndStatusComboValid, Task> {
    @Override
    public void initialize(TaskColumnAndStatusComboValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext constraintValidatorContext) {
        if (task == null || task.getBoardColumn() == null) {
            return true;
        }

        return task.getTaskStatus().equals(task.getBoardColumn().getTaskStatus());
    }
}
