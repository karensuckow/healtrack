package com.github.karensuckow.healtrack.domain.user.dto;

import com.github.karensuckow.healtrack.domain.user.model.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserRole role,
        Boolean active) {}
