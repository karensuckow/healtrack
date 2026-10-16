package com.github.karensuckow.healtrack.domain.user.repository;

import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Page<User> findAllByRole(UserRole role, Pageable pageable);

}
