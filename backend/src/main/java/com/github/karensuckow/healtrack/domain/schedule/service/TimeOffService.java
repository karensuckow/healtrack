package com.github.karensuckow.healtrack.domain.schedule.service;

import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.model.TimeOff;
import com.github.karensuckow.healtrack.domain.schedule.repository.TimeOffRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TimeOffService {

    @Autowired
    private TimeOffRepository timeOffRepository;

    public List<TimeOffResponseDTO> findTimeOffs(LocalDateTime start, LocalDateTime end, LocalDate date) {

        List<TimeOff> timeOffs;

        if (date != null) {
            LocalDateTime startOfPeriod = date.atStartOfDay();
            LocalDateTime endOfPeriod = date.atTime(LocalTime.MAX);
            timeOffs = timeOffRepository.findTimeOffsInDateRange(startOfPeriod, endOfPeriod);
        } else if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start date must not be after end date");
            }
            timeOffs = timeOffRepository.findTimeOffsInDateRange(start, end);
        } else {
            timeOffs = timeOffRepository.findAll();
        }

        return timeOffs.stream()
                .map(TimeOffResponseDTO::new)
                .toList();
    }

    @Transactional
    public TimeOffResponseDTO createTimeOff(TimeOffRequestDTO timeOffRequestDTO) {

        validateDto(timeOffRequestDTO);

        TimeOff timeOff = timeOffRequestDTO.toEntity();
        TimeOff savedTimeOff = timeOffRepository.save(timeOff);
        return new TimeOffResponseDTO(savedTimeOff);
    }

    @Transactional
    public void deleteTimeOff(Long id) {

        if (!timeOffRepository.existsById(id)) {
            throw new IllegalArgumentException("Time off not found.");
        }

        timeOffRepository.deleteById(id);
    }

    private void validateDto(TimeOffRequestDTO timeOffRequestDTO) {
        if (timeOffRequestDTO.startDate().isAfter(timeOffRequestDTO.endDate())) {
            throw new IllegalArgumentException("Start date must be after end date");
        }

        boolean hasTimeOff = timeOffRepository.existsOverlappingTimeOff(timeOffRequestDTO.startDate(), timeOffRequestDTO.endDate());
        if (hasTimeOff) {
            throw new IllegalArgumentException("Time off already exists in this period");
        }

    }
}
