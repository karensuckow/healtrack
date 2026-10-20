package com.github.karensuckow.healtrack.controller.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.TimeOffResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.service.TimeOffService;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeOffController.class)
class TimeOffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeOffService timeOffService;

    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("GET /schedule/time-off - Must return list and status 200 OK")
    @WithMockUser
    void getTimeOffs_ReturnsOk() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 19);
        TimeOffResponseDTO responseDTO = new TimeOffResponseDTO(
                1L,
                LocalDateTime.of(2026, 10, 19, 10, 0),
                LocalDateTime.of(2026, 10, 19, 16, 0),
                "Medical Appointment"
        );

        Mockito.when(timeOffService.findTimeOffs(null, null, date))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/schedule/time-off")
                        .param("date", "2026-10-19")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reason").value("Medical Appointment"));

        Mockito.verify(timeOffService).findTimeOffs(null, null, date);
    }

    @Test
    @DisplayName("POST /schedule/time-off - Must create Time off and return status 200 OK")
    @WithMockUser(roles = "ADMIN")
    void createTimeOff_ReturnsOk() throws Exception {
        TimeOffRequestDTO requestDTO = new TimeOffRequestDTO(
                LocalDateTime.of(2026, 11, 10, 14, 0),
                LocalDateTime.of(2026, 11, 10, 18, 0),
                "Congress"
        );

        TimeOffResponseDTO responseDTO = new TimeOffResponseDTO(
                1L,
                requestDTO.startDate(),
                requestDTO.endDate(),
                requestDTO.reason()
        );

        Mockito.when(timeOffService.createTimeOff(any(TimeOffRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/schedule/time-off")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reason").value("Congress"));
    }

    @Test
    @DisplayName("DELETE /schedule/time-off/{id} - Must delete and return status 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void deleteTimeOff_ReturnsNoContent() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(timeOffService).deleteTimeOff(id);

        mockMvc.perform(delete("/schedule/time-off/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(timeOffService).deleteTimeOff(id);
    }
}