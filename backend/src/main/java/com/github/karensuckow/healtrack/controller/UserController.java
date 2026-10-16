package com.github.karensuckow.healtrack.controller;

import com.github.karensuckow.healtrack.domain.user.dto.UpdatePasswordDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UpdateUserDTO;
import com.github.karensuckow.healtrack.domain.user.dto.UserResponseDTO;
import com.github.karensuckow.healtrack.domain.user.model.User;
import com.github.karensuckow.healtrack.domain.user.model.UserRole;
import com.github.karensuckow.healtrack.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserResponseDTO> users = userService.getUsersByRole(UserRole.ROLE_USER, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.softDelete(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public ResponseEntity<UserResponseDTO> updateUserById(@PathVariable Long id, @RequestBody @Valid UpdateUserDTO data) {
        return ResponseEntity.ok(userService.update(id, data));
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> reactivateUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.reactivate(id));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public ResponseEntity<UserResponseDTO> updatePassword(@PathVariable Long id, @RequestBody @Valid UpdatePasswordDTO data) {
        return ResponseEntity.ok(userService.updatePassword(id, data.password()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User loggedUser) {
        return ResponseEntity.ok(userService.getUser(loggedUser.getId()));
    }

}
