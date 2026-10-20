package com.github.karensuckow.healtrack.domain.schedule.service;

import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.model.WorkingHours;
import com.github.karensuckow.healtrack.domain.schedule.repository.WorkingHoursRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkingHoursServiceTest {

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @InjectMocks
    private WorkingHoursService workingHoursService;

    @Test
    @DisplayName("Should return a list of WorkingHoursResponseDTO when data exists")
    void shouldReturnWorkingHoursList() {
        WorkingHours workingHours = new WorkingHours(
                1L,
                (short) 1,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );
        when(workingHoursRepository.findAll()).thenReturn(List.of(workingHours));

        List<WorkingHoursResponseDTO> result = workingHoursService.getWorkingHours();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).dayOfWeek()).isEqualTo((short) 1);
        verify(workingHoursRepository).findAll();
    }

    @Test
    @DisplayName("Should return an empty list when no working hours exist")
    void shouldReturnEmptyListWhenNoWorkingHours() {
        when(workingHoursRepository.findAll()).thenReturn(Collections.emptyList());

        List<WorkingHoursResponseDTO> result = workingHoursService.getWorkingHours();

        assertThat(result).isEmpty();
        verify(workingHoursRepository).findAll();
    }

    @Test
    @DisplayName("Should clear old schedule and save new working hours")
    void shouldUpdateWorkingHoursSuccessfully() {
        WorkingHoursRequestDTO requestDTO = new WorkingHoursRequestDTO(
                (short) 2, // Tuesday
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null,
                null
        );

        WorkingHours savedEntity = new WorkingHours(
                2L,
                (short) 2,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                null,
                null
        );

        when(workingHoursRepository.saveAll(anyList())).thenReturn(List.of(savedEntity));

        List<WorkingHoursResponseDTO> result = workingHoursService.updateWorkingHours(List.of(requestDTO));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).dayOfWeek()).isEqualTo((short) 2);

        verify(workingHoursRepository).deleteAllInBatch();
        verify(workingHoursRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should delete all and return empty list when request list is empty")
    void shouldReturnEmptyListWhenUpdateWithEmptyList() {
        List<WorkingHoursResponseDTO> result = workingHoursService.updateWorkingHours(Collections.emptyList());

        assertThat(result).isEmpty();
        verify(workingHoursRepository).deleteAllInBatch();
        verify(workingHoursRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should delete all and return empty list when request list is null")
    void shouldReturnEmptyListWhenUpdateWithNull() {
        List<WorkingHoursResponseDTO> result = workingHoursService.updateWorkingHours(null);

        assertThat(result).isEmpty();
        verify(workingHoursRepository).deleteAllInBatch();
        verify(workingHoursRepository, never()).saveAll(anyList());
    }
}