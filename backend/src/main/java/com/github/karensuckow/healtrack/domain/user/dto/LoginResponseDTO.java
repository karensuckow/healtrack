package com.github.karensuckow.healtrack.domain.user.dto;

public record LoginResponseDTO(
        String token,
        String refreshToken
) {}
