package io.mattinfern0.kanbanboardapi.users.dtos;

import jakarta.validation.constraints.NotEmpty;

public record SignUpDto(
    @NotEmpty
    String firstName,

    @NotEmpty
    String lastName
) {}
