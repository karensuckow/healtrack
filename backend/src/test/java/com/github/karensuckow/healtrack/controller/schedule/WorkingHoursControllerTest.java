package com.github.karensuckow.healtrack.controller.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursRequestDTO;
import com.github.karensuckow.healtrack.domain.schedule.dto.WorkingHoursResponseDTO;
import com.github.karensuckow.healtrack.domain.schedule.service.WorkingHoursService;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.infra.security.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkingHoursController.class)
class WorkingHoursControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkingHoursService workingHoursService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("GET /schedule/working-hours - Must return the hours and status 200 OK")
    @WithMockUser
    void getWorkingHours_ReturnsOk() throws Exception {

        WorkingHoursResponseDTO responseDTO = new WorkingHoursResponseDTO(
                1L,
                (short) 1,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );

        Mockito.when(workingHoursService.getWorkingHours()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/schedule/working-hours")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].dayOfWeek").value(1))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].breakStart").value("12:00:00"));

        Mockito.verify(workingHoursService).getWorkingHours();
    }

    @Test
    @DisplayName("POST /schedule/working-hours - When admin is using, must update and return 200 OK")
    @WithMockUser(roles = "ADMIN")
    void updateWorkingHours_ReturnsOk() throws Exception {

        WorkingHoursRequestDTO requestDTO = new WorkingHoursRequestDTO(
                (short) 1,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );

        WorkingHoursResponseDTO responseDTO = new WorkingHoursResponseDTO(
                1L,
                (short) 1,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );

        List<WorkingHoursRequestDTO> requestList = List.of(requestDTO);
        List<WorkingHoursResponseDTO> responseList = List.of(responseDTO);

        Mockito.when(workingHoursService.updateWorkingHours(anyList())).thenReturn(responseList);

        mockMvc.perform(post("/schedule/working-hours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value(1))
                .andExpect(jsonPath("$[0].endTime").value("18:00:00"));

        Mockito.verify(workingHoursService).updateWorkingHours(anyList());
    }
}