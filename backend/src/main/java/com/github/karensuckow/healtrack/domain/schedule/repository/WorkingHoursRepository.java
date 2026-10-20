package com.github.karensuckow.healtrack.domain.schedule.repository;

import com.github.karensuckow.healtrack.domain.schedule.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    Optional<WorkingHours> findByDayOfWeek(Integer dayOfWeek);
}
