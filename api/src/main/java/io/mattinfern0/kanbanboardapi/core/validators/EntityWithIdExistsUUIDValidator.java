package io.mattinfern0.kanbanboardapi.core.validators;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EntityWithIdExistsUUIDValidator implements ConstraintValidator<EntityWithIdExists, UUID> {
    private Class<?> entityClass;

    private final EntityManager entityManager;

    @Autowired
    public EntityWithIdExistsUUIDValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public void initialize(EntityWithIdExists constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext constraintValidatorContext) {
        if (uuid == null) {
            return true;
        }

        return this.entityManager.find(this.entityClass, uuid) != null;
    }
}
