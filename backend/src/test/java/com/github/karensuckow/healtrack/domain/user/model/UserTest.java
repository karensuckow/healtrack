package com.github.karensuckow.healtrack.domain.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Karen", "karen@email.com", "pass12345678", UserRole.ROLE_USER, true);
    }

    @Test
    void updateData_ShouldUpdateFields_WhenValuesAreValid() {
        user.updateData("New Name", "new@email.com");

        assertEquals("New Name", user.getName());
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void updateData_ShouldNotUpdateFields_WhenValuesAreNullOrBlank() {
        user.updateData(null, "");

        assertEquals("Karen", user.getName());
        assertEquals("karen@email.com", user.getEmail());
    }

    @Test
    void updatePassword_ShouldUpdate_WhenValueIsValid() {
        user.updatePassword("newStrongPass");

        assertEquals("newStrongPass", user.getPassword());
    }

    @Test
    void updatePassword_ShouldNotUpdate_WhenValueIsNullOrBlank() {
        user.updatePassword("   ");

        assertEquals("pass12345678", user.getPassword());
    }

    @Test
    void setInactive_ShouldSetStatusFalseAndSetInactiveAt() {
        assertNull(user.getInactiveAt());

        user.setInactive();

        assertFalse(user.getActive());
        assertNotNull(user.getInactiveAt());
        assertTrue(user.getInactiveAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void setActive_ShouldSetStatusTrueAndClearInactiveAt() {
        user.setInactive();
        assertNotNull(user.getInactiveAt());

        user.setActive();

        assertTrue(user.getActive());
        assertNull(user.getInactiveAt());
    }

    @Test
    void getAuthorities_ShouldReturnAdminAndUserRoles_WhenUserIsAdmin() {
        User adminUser = new User("Admin", "admin@email.com", "12345678", UserRole.ROLE_ADMIN, true);

        Collection<? extends GrantedAuthority> authorities = adminUser.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        assertTrue(authorities.stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority())));
    }

    @Test
    void getAuthorities_ShouldReturnOnlyUserRole_WhenUserIsStandardUser() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority())));
    }

    @Test
    void getUsername_ShouldReturnUserEmail() {
        assertEquals("karen@email.com", user.getUsername());
    }

    @Test
    void isEnabled_ShouldReturnActiveStatus() {
        assertTrue(user.isEnabled());

        user.setInactive();

        assertFalse(user.isEnabled());
    }
}