package com.github.karensuckow.healtrack.domain.schedule.dto;

import com.github.karensuckow.healtrack.domain.schedule.model.WorkingHours;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record WorkingHoursRequestDTO(
        @NotNull(message = "Day of week is required")
        @Min(value = 1, message = "Day of week must be between 1 (monday) and 7 (sunday)")
        @Max(value = 7, message = "Day of week must be between 1 (monday) and 7 (sunday)")
        Short dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        LocalTime breakStart,
        LocalTime breakEnd
) {
        public WorkingHours toEntity() {
            WorkingHours entity = new WorkingHours();

            entity.setDayOfWeek(this.dayOfWeek);
            entity.setStartTime(this.startTime);
            entity.setEndTime(this.endTime);
            entity.setBreakStart(this.breakStart);
            entity.setBreakEnd(this.breakEnd);

            return entity;
        }
}