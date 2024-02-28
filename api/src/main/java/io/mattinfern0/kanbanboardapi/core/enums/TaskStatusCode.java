package io.mattinfern0.kanbanboardapi.core.enums;

public enum TaskStatusCode {
    BACKLOG("BACKLOG"),
    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    OTHER("OTHER");


    private final String value;

    TaskStatusCode(final String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
