package com.github.karensuckow.healtrack.domain.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeOffTest {

    @Test
    @DisplayName("Should create TimeOff entity using no-args constructor and setters")
    void shouldCreateTimeOffUsingNoArgsConstructorAndSetters() {
        TimeOff timeOff = new TimeOff();
        LocalDateTime start = LocalDateTime.of(2026, 12, 20, 8, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 25, 18, 0);

        timeOff.setId(1L);
        timeOff.setStartDate(start);
        timeOff.setEndDate(end);
        timeOff.setReason("End of year vacation");

        assertThat(timeOff.getId()).isEqualTo(1L);
        assertThat(timeOff.getStartDate()).isEqualTo(start);
        assertThat(timeOff.getEndDate()).isEqualTo(end);
        assertThat(timeOff.getReason()).isEqualTo("End of year vacation");
    }

    @Test
    @DisplayName("Should create TimeOff entity using all-args constructor")
    void shouldCreateTimeOffUsingAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 10, 18, 0);

        TimeOff timeOff = new TimeOff(
                2L,
                start,
                end,
                "Holistic Therapies Congress"
        );

        assertThat(timeOff.getId()).isEqualTo(2L);
        assertThat(timeOff.getStartDate()).isEqualTo(start);
        assertThat(timeOff.getEndDate()).isEqualTo(end);
        assertThat(timeOff.getReason()).isEqualTo("Holistic Therapies Congress");
    }
}