package io.mattinfern0.kanbanboardapi.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    LOW(1, "LOW"),
    MEDIUM(2, "MEDIUM"),
    HIGH(3, "HIGH");

    private final Integer databaseId;
    private final String codename;

    TaskPriority(final Integer databaseId, final String codename) {
        this.databaseId = databaseId;
        this.codename = codename;
    }

    public static TaskPriority fromDatabaseId(Integer databaseId) {
        for (TaskPriority taskPriority : TaskPriority.values()) {
            if (taskPriority.getDatabaseId().equals(databaseId)) {
                return taskPriority;
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
