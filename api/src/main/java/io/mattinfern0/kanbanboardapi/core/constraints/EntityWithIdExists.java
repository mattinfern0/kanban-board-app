package io.mattinfern0.kanbanboardapi.core.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = {EntityWithIdExistsUUIDValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityWithIdExists {
    String message() default "{entityClass} entity with id ${validatedValue} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entityClass();
}
