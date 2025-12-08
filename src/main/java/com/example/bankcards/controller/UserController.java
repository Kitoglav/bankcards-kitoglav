package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUser(final Authentication authentication) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            return ResponseEntity.ok(UserResponse.of(user));
        });
    }
}
