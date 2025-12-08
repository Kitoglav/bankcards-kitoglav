package com.example.bankcards.controller;

import com.example.bankcards.dto.login.JwtData;
import com.example.bankcards.dto.login.LoginRequest;
import com.example.bankcards.dto.login.LoginResponse;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.util.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для входа в систему")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и получение JWT токена")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))), @ApiResponse(responseCode = "400", description = "Неверные данные для входа"), @ApiResponse(responseCode = "401", description = "Неверные учетные данные")})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Parameter(description = "Данные для входа", required = true) @RequestBody @Validated LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        JwtData jwt = jwtProvider.generateToken(userDetails);
        return ResponseEntity.ok(LoginResponse.of(userDetails.getId(), jwt));
    }
}