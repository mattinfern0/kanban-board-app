package io.mattinfern0.kanbanboardapi.core.constraints;

import io.mattinfern0.kanbanboardapi.core.validators.EntityWithIdExistsUUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {EntityWithIdExistsUUIDValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityWithIdExists {
    String message() default "Entity with id ${validatedValue} not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entityClass();
}
