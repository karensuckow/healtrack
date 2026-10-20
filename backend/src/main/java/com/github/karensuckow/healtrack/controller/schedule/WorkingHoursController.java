package com.github.karensuckow.healtrack.controller.schedule;

import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.service.WorkingHoursService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/working-hours")
public class WorkingHoursController {

    @Autowired
    private WorkingHoursService workingHoursService;

    @GetMapping
    public ResponseEntity<List<WorkingHoursResponseDTO>> getWorkingHours() {
        var workingHours = workingHoursService.getWorkingHours();
        return ResponseEntity.ok(workingHours);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WorkingHoursResponseDTO>> updateWorkingHours(@RequestBody @Valid List<@Valid WorkingHoursRequestDTO> workingHoursRequestDTOList) {
        var workingHours = workingHoursService.updateWorkingHours(workingHoursRequestDTOList);
        return ResponseEntity.ok(workingHours);
    }

}
