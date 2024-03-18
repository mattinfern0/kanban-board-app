package io.mattinfern0.kanbanboardapi.boards.contraints;

import io.mattinfern0.kanbanboardapi.boards.validators.BoardColumnHasTasksColumnReorderDtoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = {BoardColumnHasTasksColumnReorderDtoValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface BoardColumnOrderTasksOrderOnlyHasAllColumnTasks {
    String message() default "Task order list must only contain all tasks in the column.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
