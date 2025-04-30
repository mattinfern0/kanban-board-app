package io.mattinfern0.kanbanboardapi.core.converters;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrganizationRoleConverter implements AttributeConverter<OrganizationRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(@Nullable OrganizationRole taskPriority) {
        if (taskPriority == null) {
            return null;
        }
        return taskPriority.getDatabaseId();
    }

    @Override
    public OrganizationRole convertToEntityAttribute(Integer integer) {
        try {
            return OrganizationRole.fromDatabaseId(integer);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
