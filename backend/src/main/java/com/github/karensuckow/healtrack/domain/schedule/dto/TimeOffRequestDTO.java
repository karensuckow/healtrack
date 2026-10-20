package com.github.karensuckow.healtrack.domain.schedule.dto;

import com.github.karensuckow.healtrack.domain.schedule.model.TimeOff;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TimeOffRequestDTO(
        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be a past date")
        LocalDateTime startDate,

        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date cannot be a past date")
        LocalDateTime endDate,

        @NotBlank(message = "The reason is required")
        String reason
) {
        public TimeOff toEntity() {
            TimeOff entity = new TimeOff();

            entity.setStartDate(this.startDate);
            entity.setEndDate(this.endDate);
            entity.setReason(this.reason);

            return entity;
        }
}