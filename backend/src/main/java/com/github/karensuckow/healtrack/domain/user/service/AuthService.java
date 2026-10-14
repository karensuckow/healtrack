package com.github.karensuckow.healtrack.domain.user.service;

import com.github.karensuckow.healtrack.domain.user.dto.LoginRequestDTO;
import com.github.karensuckow.healtrack.domain.user.dto.LoginResponseDTO;
import com.github.karensuckow.healtrack.domain.user.dto.RegisterRequestDTO;
import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import com.github.karensuckow.healtrack.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    public LoginResponseDTO authenticate(LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = authenticationManager.authenticate(usernamePassword);
        var user = (User) auth.getPrincipal();

        var token = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);

        return new LoginResponseDTO(token, refreshToken);
    }

    public void register(RegisterRequestDTO data) {

        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        var user = new User(data.name(), data.email(), encryptedPassword, UserRole.ROLE_USER, true);
        userRepository.save(user);

    }

}
