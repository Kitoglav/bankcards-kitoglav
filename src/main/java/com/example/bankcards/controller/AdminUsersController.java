package com.example.bankcards.controller;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.admin.CreateUserRequest;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.service.UserService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Validated CreateUserRequest request) {
        User user = userService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable @Min(1) Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(UserResponse.of(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable @Min(1) Long userId) {
        try {
            User user = userService.deleteUser(userId);
            return ResponseEntity.ok(UserResponse.of(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }
}
