package io.mattinfern0.kanbanboardapi.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrganizationRole {
    OWNER(1, "OWNER"),
    MEMBER(2, "MEMBER");

    private final Integer databaseId;
    private final String codename;


    OrganizationRole(Integer databaseId, String codename) {
        this.databaseId = databaseId;
        this.codename = codename;
    }

    public static OrganizationRole fromDatabaseId(Integer databaseId) {
        for (OrganizationRole organizationRole : OrganizationRole.values()) {
            if (organizationRole.getDatabaseId().equals(databaseId)) {
                return organizationRole;
            }
        }

        throw new IllegalArgumentException("No TaskPriority with databaseId " + databaseId + " exists.");
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
