package com.github.karensuckow.healtrack.domain.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkingHoursTest {

    @Test
    @DisplayName("Should create WorkingHours entity using no-args constructor and setters")
    void shouldCreateWorkingHoursUsingNoArgsConstructorAndSetters() {
        WorkingHours workingHours = new WorkingHours();
        Short dayOfWeek = 1; // 1 = Monday
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        LocalTime breakStart = LocalTime.of(12, 0);
        LocalTime breakEnd = LocalTime.of(13, 0);

        workingHours.setId(1L);
        workingHours.setDayOfWeek(dayOfWeek);
        workingHours.setStartTime(startTime);
        workingHours.setEndTime(endTime);
        workingHours.setBreakStart(breakStart);
        workingHours.setBreakEnd(breakEnd);

        assertThat(workingHours.getId()).isEqualTo(1L);
        assertThat(workingHours.getDayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(workingHours.getStartTime()).isEqualTo(startTime);
        assertThat(workingHours.getEndTime()).isEqualTo(endTime);
        assertThat(workingHours.getBreakStart()).isEqualTo(breakStart);
        assertThat(workingHours.getBreakEnd()).isEqualTo(breakEnd);
    }

    @Test
    @DisplayName("Should create WorkingHours entity using all-args constructor")
    void shouldCreateWorkingHoursUsingAllArgsConstructor() {
        Short dayOfWeek = 3; // 3 = Wednesday
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(19, 0);
        LocalTime breakStart = LocalTime.of(13, 0);
        LocalTime breakEnd = LocalTime.of(14, 0);

        WorkingHours workingHours = new WorkingHours(
                2L,
                dayOfWeek,
                startTime,
                endTime,
                breakStart,
                breakEnd
        );

        assertThat(workingHours.getId()).isEqualTo(2L);
        assertThat(workingHours.getDayOfWeek()).isEqualTo(dayOfWeek);
        assertThat(workingHours.getStartTime()).isEqualTo(startTime);
        assertThat(workingHours.getEndTime()).isEqualTo(endTime);
        assertThat(workingHours.getBreakStart()).isEqualTo(breakStart);
        assertThat(workingHours.getBreakEnd()).isEqualTo(breakEnd);
    }
}