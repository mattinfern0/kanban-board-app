package io.mattinfern0.kanbanboardapi.users.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDto(
    @NotNull
    UUID id,

    @NotEmpty
    String firstName,

    @NotEmpty
    String lastName
) {
}
