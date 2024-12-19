package io.mattinfern0.kanbanboardapi.core.converters;

import io.mattinfern0.kanbanboardapi.core.enums.TaskPriority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskPriority taskPriority) {
        return taskPriority.getDatabaseId();
    }

    @Override
    public TaskPriority convertToEntityAttribute(Integer integer) {
        try {
            return TaskPriority.fromDatabaseId(integer);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
