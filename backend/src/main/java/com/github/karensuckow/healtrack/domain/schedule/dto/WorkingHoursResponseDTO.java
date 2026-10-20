package com.github.karensuckow.healtrack.domain.schedule.dto;

import com.github.karensuckow.healtrack.domain.schedule.model.WorkingHours;
import java.time.LocalTime;

public record WorkingHoursResponseDTO(
        Long id,
        Short dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime breakStart,
        LocalTime breakEnd
) {
    public WorkingHoursResponseDTO(WorkingHours entity) {
        this(entity.getId(), entity.getDayOfWeek(), entity.getStartTime(),
                entity.getEndTime(), entity.getBreakStart(), entity.getBreakEnd());
    }
}