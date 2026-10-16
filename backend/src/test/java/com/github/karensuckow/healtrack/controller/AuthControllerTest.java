package com.github.karensuckow.healtrack.controller;

import com.github.karensuckow.healtrack.domain.user.dto.LoginRequestDTO;
import com.github.karensuckow.healtrack.domain.user.dto.LoginResponseDTO;
import com.github.karensuckow.healtrack.domain.user.dto.RegisterRequestDTO;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.domain.user.service.AuthService;
import com.github.karensuckow.healtrack.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void login_ShouldReturn200AndToken_WhenCredentialsAreValid() throws Exception {
        var request = new LoginRequestDTO("teste@email.com", "12345678");
        var response = new LoginResponseDTO("token-jwt-falso-aqui", "refresh-token-jwt-falso-aqui");

        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void register_ShouldReturn201_WhenRegistrationIsSuccessful() throws Exception {
        var request = new RegisterRequestDTO("Karen", "teste@email.com", "12345678");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_ShouldReturn400_WhenServiceThrowsException() throws Exception {
        var request = new RegisterRequestDTO("Karen", "teste@email.com", "12345678");

        doThrow(new IllegalArgumentException("Email already exists"))
                .when(authService).register(any(RegisterRequestDTO.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void register_ShouldReturn400_WhenPayloadIsInvalid() throws Exception {
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO("", "email-invalido", "123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        org.mockito.Mockito.verifyNoInteractions(authService);
    }
}