package com.github.karensuckow.healtrack.infra.security;

import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private User mockUser;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();

        ReflectionTestUtils.setField(tokenService, "secret", "my-super-secret-test-key-12345");
        ReflectionTestUtils.setField(tokenService, "expiration", 7200L); // 2 hours
        ReflectionTestUtils.setField(tokenService, "refreshExpiration", 86400L); // 24 hours

        mockUser = new User("Karen", "karen@email.com", "password12345678", UserRole.ROLE_USER, true);
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        String token = tokenService.generateToken(mockUser);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateRefreshToken_ShouldReturnValidJwtToken() {
        String refreshToken = tokenService.generateRefreshToken(mockUser);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isBlank());
        assertEquals(3, refreshToken.split("\\.").length);
    }

    @Test
    void validateToken_ShouldReturnSubject_WhenTokenIsValid() {
        String token = tokenService.generateToken(mockUser);

        String subject = tokenService.validateToken(token);

        assertEquals("karen@email.com", subject);
    }

    @Test
    void validateToken_ShouldThrowException_WhenTokenIsTamperedOrInvalid() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.invalidSignature";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(invalidToken);
        });

        assertEquals("Error validating token", exception.getMessage());
    }

    @Test
    void validateToken_ShouldThrowException_WhenSecretIsDifferent() {
        String token = tokenService.generateToken(mockUser);

        ReflectionTestUtils.setField(tokenService, "secret", "wrong-secret-key");

        assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(token);
        });
    }
}