package io.mattinfern0.kanbanboardapi.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrganizationInviteStatus {
    PENDING(1, "PENDING"),
    ACCEPTED(2, "ACCEPTED"),
    REVOKED(3, "REVOKED");

    private final Integer databaseId;
    private final String codename;

    OrganizationInviteStatus(final Integer databaseId, final String codename) {
        this.databaseId = databaseId;
        this.codename = codename;
    }

    public static OrganizationInviteStatus fromDatabaseId(Integer databaseId) {
        for (OrganizationInviteStatus taskPriority : OrganizationInviteStatus.values()) {
            if (taskPriority.getDatabaseId().equals(databaseId)) {
                return taskPriority;
            }
        }

        throw new IllegalArgumentException("No OrganizationInviteStatus with databaseId " + databaseId + " exists.");
    }

    @Override
    public String toString() {
        return this.codename;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    @JsonValue
    public String getCodename() {
        return codename;
    }
}
