package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Информация о пользователе")

public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает информацию о текущем аутентифицированном пользователе")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Информация получена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))), @ApiResponse(responseCode = "401", description = "Не авторизован")})
    @GetMapping
    public ResponseEntity<UserResponse> getUser(@Parameter(hidden = true) final Authentication authentication) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            return ResponseEntity.ok(UserResponse.of(user));
        });
    }
}