package com.github.karensuckow.healtrack.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password) {
}
