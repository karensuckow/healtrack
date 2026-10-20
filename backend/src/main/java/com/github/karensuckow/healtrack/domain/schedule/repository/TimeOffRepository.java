package com.github.karensuckow.healtrack.domain.schedule.repository;

import com.github.karensuckow.healtrack.domain.schedule.model.TimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TimeOffRepository extends JpaRepository<TimeOff, Long> {

    @Query("SELECT t FROM TimeOff t WHERE t.startDate <= :end AND t.endDate >= :start")
    List<TimeOff> findTimeOffsInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TimeOff t " +
            "WHERE t.startDate < :newEnd AND t.endDate > :newStart")
    boolean existsOverlappingTimeOff(@Param("newStart") LocalDateTime newStart,
                                     @Param("newEnd") LocalDateTime newEnd);

}
