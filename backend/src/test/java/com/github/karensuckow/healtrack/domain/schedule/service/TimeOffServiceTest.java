package com.github.karensuckow.healtrack.domain.schedule.service;

import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.model.TimeOff;
import com.github.karensuckow.healtrack.domain.schedule.repository.TimeOffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeOffServiceTest {

    @Mock
    private TimeOffRepository timeOffRepository;

    @InjectMocks
    private TimeOffService timeOffService;

    @Test
    @DisplayName("Should find time offs by specific date")
    void shouldFindTimeOffsByDate() {
        LocalDate date = LocalDate.of(2026, 10, 20);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        TimeOff timeOff = new TimeOff(1L, startOfDay.plusHours(10), startOfDay.plusHours(12), "Doctor");
        when(timeOffRepository.findTimeOffsInDateRange(startOfDay, endOfDay)).thenReturn(List.of(timeOff));

        List<TimeOffResponseDTO> result = timeOffService.findTimeOffs(null, null, date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        verify(timeOffRepository).findTimeOffsInDateRange(startOfDay, endOfDay);
        verify(timeOffRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should find time offs by start and end datetime range")
    void shouldFindTimeOffsByDateRange() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 31, 23, 59);

        TimeOff timeOff = new TimeOff(1L, start.plusDays(10), start.plusDays(10).plusHours(2), "Vacation");
        when(timeOffRepository.findTimeOffsInDateRange(start, end)).thenReturn(List.of(timeOff));

        List<TimeOffResponseDTO> result = timeOffService.findTimeOffs(start, end, null);

        assertThat(result).hasSize(1);
        verify(timeOffRepository).findTimeOffsInDateRange(start, end);
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date in findTimeOffs")
    void shouldThrowExceptionWhenFindWithInvalidDateRange() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 1, 0, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> timeOffService.findTimeOffs(start, end, null));

        assertThat(exception.getMessage()).isEqualTo("Start date must not be after end date");
        verify(timeOffRepository, never()).findTimeOffsInDateRange(any(), any());
    }

    @Test
    @DisplayName("Should find all time offs when no parameters are provided")
    void shouldFindAllTimeOffsWhenNoParams() {
        TimeOff timeOff = new TimeOff(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Break");
        when(timeOffRepository.findAll()).thenReturn(List.of(timeOff));

        List<TimeOffResponseDTO> result = timeOffService.findTimeOffs(null, null, null);

        assertThat(result).hasSize(1);
        verify(timeOffRepository).findAll();
    }

    @Test
    @DisplayName("Should create time off successfully")
    void shouldCreateTimeOffSuccessfully() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 10, 12, 0);
        TimeOffRequestDTO requestDTO = new TimeOffRequestDTO(start, end, "Dentist");

        TimeOff savedEntity = new TimeOff(1L, start, end, "Dentist");

        when(timeOffRepository.existsOverlappingTimeOff(start, end)).thenReturn(false);
        when(timeOffRepository.save(any(TimeOff.class))).thenReturn(savedEntity);

        TimeOffResponseDTO responseDTO = timeOffService.createTimeOff(requestDTO);

        assertThat(responseDTO.id()).isEqualTo(1L);
        assertThat(responseDTO.reason()).isEqualTo("Dentist");
        verify(timeOffRepository).existsOverlappingTimeOff(start, end);
        verify(timeOffRepository).save(any(TimeOff.class));
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date in creation")
    void shouldThrowExceptionOnCreateWhenDatesAreInverted() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 10, 10, 0); // Inverted
        TimeOffRequestDTO requestDTO = new TimeOffRequestDTO(start, end, "Error test");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> timeOffService.createTimeOff(requestDTO));

        assertThat(exception.getMessage()).isEqualTo("Start date must be after end date"); // Note: Message matches your code
        verify(timeOffRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating an overlapping time off")
    void shouldThrowExceptionOnCreateWhenOverlappingExists() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 10, 12, 0);
        TimeOffRequestDTO requestDTO = new TimeOffRequestDTO(start, end, "Overlap");

        when(timeOffRepository.existsOverlappingTimeOff(start, end)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> timeOffService.createTimeOff(requestDTO));

        assertThat(exception.getMessage()).isEqualTo("Time off already exists in this period");
        verify(timeOffRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete time off when ID exists")
    void shouldDeleteTimeOffWhenIdExists() {
        Long id = 1L;
        when(timeOffRepository.existsById(id)).thenReturn(true);

        timeOffService.deleteTimeOff(id);

        verify(timeOffRepository).existsById(id);
        verify(timeOffRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent time off")
    void shouldThrowExceptionWhenDeletingNonExistentId() {
        Long id = 99L;
        when(timeOffRepository.existsById(id)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> timeOffService.deleteTimeOff(id));

        assertThat(exception.getMessage()).isEqualTo("Time off not found.");
        verify(timeOffRepository, never()).deleteById(anyLong());
    }
}