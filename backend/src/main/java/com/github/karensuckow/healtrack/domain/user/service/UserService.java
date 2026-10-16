package com.github.karensuckow.healtrack.domain.user.service;

import com.github.karensuckow.healtrack.domain.user.dto.UpdateUserDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UserResponseDTO;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserResponseDTO getUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    public Page<UserResponseDTO> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findAllByRole(role, pageable)
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled()));
    }

    @Transactional
    public UserResponseDTO update(Long id, UpdateUserDTO data) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(data.email() != null && !user.getEmail().equals(data.email())) {
            if(userRepository.findByEmail(data.email()).isPresent()) {
                throw new IllegalArgumentException("Email already in use");
            }
        }

        user.updateData(data.name(), data.email());

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    @Transactional
    public UserResponseDTO updatePassword(Long id, String newPassword) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        String encryptedPassword = passwordEncoder.encode(newPassword);

        user.updatePassword(encryptedPassword);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    @Transactional
    public UserResponseDTO softDelete(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setInactive();
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    @Transactional
    public UserResponseDTO reactivate(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setActive();
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
    }
}
