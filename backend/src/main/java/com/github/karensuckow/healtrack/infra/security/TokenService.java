package com.github.karensuckow.healtrack.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.karensuckow.healtrack.domain.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private Long expiration;

    @Value("${api.security.token.refresh-expiration}")
    private Long refreshExpiration;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("healtrack")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("healtrack")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error validating token", e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("healtrack")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genRefreshExpirationDate())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Error generating refresh token", e);
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusSeconds(expiration).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant genRefreshExpirationDate() {
        return LocalDateTime.now().plusSeconds(refreshExpiration).toInstant(ZoneOffset.of("-03:00"));
    }

}
