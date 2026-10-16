package com.github.karensuckow.healtrack.domain.user.service;

import com.github.karensuckow.healtrack.domain.user.dto.UpdateUserDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UserResponseDTO;
import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("Karen", "karen@email.com", "password12345678", UserRole.ROLE_USER, true);
        mockUser.setId(1L);
    }

    @Test
    void getUser_ShouldReturnUserResponseDTO_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponseDTO response = userService.getUser(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Karen", response.name());
        assertEquals("karen@email.com", response.email());
        assertEquals(UserRole.ROLE_USER, response.role());
        assertTrue(response.active());
    }

    @Test
    void getUser_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void getUsersByRole_ShouldReturnPagedUserResponseDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(mockUser));
        when(userRepository.findAllByRole(UserRole.ROLE_USER, pageable)).thenReturn(userPage);

        Page<UserResponseDTO> responsePage = userService.getUsersByRole(UserRole.ROLE_USER, pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Karen", responsePage.getContent().get(0).name());
    }

    @Test
    void update_ShouldUpdateUserData_WhenPayloadIsValidAndEmailIsNew() {
        UpdateUserDTO updateDTO = new UpdateUserDTO("Karen Updated", "new@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());

        UserResponseDTO response = userService.update(1L, updateDTO);

        assertNotNull(response);
        assertEquals("Karen Updated", response.name());
        assertEquals("new@email.com", response.email());
        verify(userRepository).findByEmail("new@email.com");
    }

    @Test
    void update_ShouldUpdateUserData_WhenEmailIsTheSame() {
        UpdateUserDTO updateDTO = new UpdateUserDTO("Karen Updated", "karen@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponseDTO response = userService.update(1L, updateDTO);

        assertNotNull(response);
        assertEquals("Karen Updated", response.name());
        assertEquals("karen@email.com", response.email());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenNewEmailIsAlreadyInUse() {
        UpdateUserDTO updateDTO = new UpdateUserDTO("Karen", "other@email.com");
        User existingOtherUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("other@email.com")).thenReturn(Optional.of(existingOtherUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(1L, updateDTO));
    }

    @Test
    void updatePassword_ShouldEncodeAndSetNewPassword_WhenValid() {
        String newPassword = "newStrongPassword";
        String encodedPassword = "$2a$10$fakeEncodedPasswordHash";

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        UserResponseDTO response = userService.updatePassword(1L, newPassword);

        assertNotNull(response);
        verify(passwordEncoder).encode(newPassword);
        assertEquals(encodedPassword, mockUser.getPassword());
    }

    @Test
    void updatePassword_ShouldThrowIllegalArgumentException_WhenPasswordIsBlank() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword(1L, "   "));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void softDelete_ShouldSetUserAsInactive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponseDTO response = userService.softDelete(1L);

        assertNotNull(response);
        assertFalse(response.active());
        assertFalse(mockUser.getActive());
        assertNotNull(mockUser.getInactiveAt());
    }

    @Test
    void reactivate_ShouldSetUserAsActive() {
        mockUser.setInactive();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponseDTO response = userService.reactivate(1L);

        assertNotNull(response);
        assertTrue(response.active());
        assertTrue(mockUser.getActive());
        assertNull(mockUser.getInactiveAt());
    }
}