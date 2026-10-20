package com.github.karensuckow.healtrack.domain.schedule.dto;

import com.github.karensuckow.healtrack.domain.schedule.model.TimeOff;
import java.time.LocalDateTime;

public record TimeOffResponseDTO(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String reason
) {
    public TimeOffResponseDTO(TimeOff entity) {
        this(entity.getId(), entity.getStartDate(), entity.getEndDate(), entity.getReason());
    }
}