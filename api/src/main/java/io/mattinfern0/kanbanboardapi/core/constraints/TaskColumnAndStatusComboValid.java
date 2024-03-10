package io.mattinfern0.kanbanboardapi.core.constraints;

import io.mattinfern0.kanbanboardapi.core.validators.TaskEntityColumnAndStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = {
    TaskEntityColumnAndStatusValidator.class
})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskColumnAndStatusComboValid {
    String message() default "Task status and boardColumn combination not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
