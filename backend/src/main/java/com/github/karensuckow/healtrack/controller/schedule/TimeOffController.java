package com.github.karensuckow.healtrack.controller.schedule;

import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.service.TimeOffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/schedule/time-off")
public class TimeOffController {

    @Autowired
    private TimeOffService timeOffService;

    @GetMapping
    public ResponseEntity<List<TimeOffResponseDTO>> getTimeOffs(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<TimeOffResponseDTO> timeOffs = timeOffService.findTimeOffs(start, end, date);
        return ResponseEntity.ok(timeOffs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimeOffResponseDTO> createTimeOff(@RequestBody @Valid TimeOffRequestDTO timeOffRequestDTO) {
        TimeOffResponseDTO timeOff = timeOffService.createTimeOff(timeOffRequestDTO);
        return ResponseEntity.ok(timeOff);
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTimeOff(@PathVariable Long id) {
        timeOffService.deleteTimeOff(id);
        return ResponseEntity.noContent().build();
    }


}
