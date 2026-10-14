package com.github.karensuckow.healtrack.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(min = 3, message = "Name must be at least 3 characters long")
        String name,

        @Email(message = "Email is invalid")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password ) {
}
