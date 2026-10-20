package com.github.karensuckow.healtrack.domain.schedule.service;

import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.model.WorkingHours;
import com.github.karensuckow.healtrack.domain.schedule.repository.WorkingHoursRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WorkingHoursService {

    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    public List<WorkingHoursResponseDTO> getWorkingHours() {
        return workingHoursRepository.findAll().stream()
                .map(WorkingHoursResponseDTO::new)
                .toList();
    }

    @Transactional
    public List<WorkingHoursResponseDTO> updateWorkingHours(List<WorkingHoursRequestDTO> workingHoursRequestDTOList) {

        workingHoursRepository.deleteAllInBatch();

        if (workingHoursRequestDTOList == null || workingHoursRequestDTOList.isEmpty()) {
            return Collections.emptyList();
        }

        List<WorkingHours> workingHoursToSave = workingHoursRequestDTOList.stream()
                .map(WorkingHoursRequestDTO::toEntity)
                .toList();

        List<WorkingHours> newHours = workingHoursRepository.saveAll(workingHoursToSave);

        return newHours.stream()
                .map(WorkingHoursResponseDTO::new)
                .toList();
    }

}
