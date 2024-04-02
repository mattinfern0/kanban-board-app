package io.mattinfern0.kanbanboardapi.core.enums;

public enum OrganizationRoleCodename {
    OWNER("OWNER"),
    MEMBER("MEMBER");

    private final String value;


    OrganizationRoleCodename(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
