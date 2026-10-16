package com.github.karensuckow.healtrack.controller;

import com.github.karensuckow.healtrack.domain.user.dto.UpdatePasswordDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UpdateUserDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UserResponseDTO;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.domain.user.service.UserService;
import com.github.karensuckow.healtrack.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private UserResponseDTO createMockUserResponse() {
        return new UserResponseDTO(1L, "Karen", "karen@email.com", UserRole.ROLE_USER, true);
    }

    @Test
    void getAllUsers_ShouldReturn200AndPageOfUsers() throws Exception {
        UserResponseDTO mockUser = createMockUserResponse();
        Page<UserResponseDTO> userPage = new PageImpl<>(List.of(mockUser));

        when(userService.getUsersByRole(eq(UserRole.ROLE_USER), any(Pageable.class)))
                .thenReturn(userPage);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("karen@email.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getUserById_ShouldReturn200AndUser_WhenFound() throws Exception {
        Long userId = 1L;
        UserResponseDTO response = createMockUserResponse();
        when(userService.getUser(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deleteUserById_ShouldReturn200AndSoftDeletedUser() throws Exception {
        Long userId = 1L;
        UserResponseDTO response = new UserResponseDTO(userId, "Karen", "karen@email.com", UserRole.ROLE_USER, false);
        when(userService.softDelete(userId)).thenReturn(response);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateUserById_ShouldReturn200AndUpdatedUser_WhenPayloadIsValid() throws Exception {
        Long userId = 1L;

        UpdateUserDTO updateRequest = new UpdateUserDTO("Karen Updated", "karen@email.com");
        UserResponseDTO response = new UserResponseDTO(userId, "Karen Updated", "karen@email.com", UserRole.ROLE_USER, true);

        when(userService.update(eq(userId), any(UpdateUserDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateUserById_ShouldReturn400_WhenPayloadIsInvalid() throws Exception {
        Long userId = 1L;
        UpdateUserDTO invalidRequest = new UpdateUserDTO("", "invalid-email");

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void reactivateUserById_ShouldReturn200AndReactivatedUser() throws Exception {
        Long userId = 1L;
        UserResponseDTO response = createMockUserResponse();
        when(userService.reactivate(userId)).thenReturn(response);

        mockMvc.perform(patch("/users/{id}/reactivate", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updatePassword_ShouldReturn200_WhenPasswordIsValid() throws Exception {
        Long userId = 1L;
        UpdatePasswordDTO passwordRequest = new UpdatePasswordDTO("newStrongPassword123");
        UserResponseDTO response = createMockUserResponse();

        when(userService.updatePassword(userId, "newStrongPassword123")).thenReturn(response);

        mockMvc.perform(patch("/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updatePassword_ShouldReturn400_WhenPasswordIsInvalid() throws Exception {
        Long userId = 1L;
        UpdatePasswordDTO invalidPasswordRequest = new UpdatePasswordDTO("12");

        mockMvc.perform(patch("/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPasswordRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}