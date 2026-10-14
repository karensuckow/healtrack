package com.github.karensuckow.healtrack.domain.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "inactive_at")
    private LocalDateTime inactiveAt;

    public User(String name, String email, String password, UserRole role, boolean active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public void updateData(String newName, String newEmail, String newPassword) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }

        if (newEmail != null && !newEmail.isBlank()) {
            this.email = newEmail;
        }

        if (newPassword != null && !newPassword.isBlank()) {
            this.password = newPassword;
        }

    }

    public void setInactive() {
        this.active = false;
        this.inactiveAt = LocalDateTime.now();
    }

    public void setActive() {
        this.active = true;
        this.inactiveAt = null;
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ROLE_ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
