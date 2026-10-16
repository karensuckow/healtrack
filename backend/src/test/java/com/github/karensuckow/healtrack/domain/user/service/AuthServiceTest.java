package com.github.karensuckow.healtrack.domain.user.service;

import com.github.karensuckow.healtrack.domain.user.dto.LoginRequestDTO;
import com.github.karensuckow.healtrack.domain.user.dto.LoginResponseDTO;
import com.github.karensuckow.healtrack.domain.user.dto.RegisterRequestDTO;
import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_ShouldReturnTokens_WhenCredentialsAreValid() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("karen@email.com", "password12345678");
        User mockUser = new User("Karen", "karen@email.com", "encrypted-pass", UserRole.ROLE_USER, true);

        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(tokenService.generateToken(mockUser)).thenReturn("fake-access-token");
        when(tokenService.generateRefreshToken(mockUser)).thenReturn("fake-refresh-token");

        LoginResponseDTO response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("fake-access-token", response.token());
        assertEquals("fake-refresh-token", response.refreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_ShouldSaveNewUser_WhenEmailIsUnique() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Karen", "karen@email.com", "password12345678");
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(registerRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Karen", savedUser.getName());
        assertEquals("karen@email.com", savedUser.getEmail());
        assertEquals(UserRole.ROLE_USER, savedUser.getRole());
        assertTrue(savedUser.getActive());

        assertNotEquals("password12345678", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2a$"));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("Karen", "karen@email.com", "password12345678");
        User existingUser = new User();
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}