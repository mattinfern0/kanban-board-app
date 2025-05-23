package io.mattinfern0.kanbanboardapi.core.converters;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrganizationInviteStatusConverter implements AttributeConverter<OrganizationInviteStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(@Nullable OrganizationInviteStatus inviteStatus) {
        if (inviteStatus == null) {
            return null;
        }
        return inviteStatus.getDatabaseId();
    }

    @Override
    public OrganizationInviteStatus convertToEntityAttribute(Integer integer) {
        try {
            return OrganizationInviteStatus.fromDatabaseId(integer);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
