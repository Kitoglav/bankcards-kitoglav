package com.example.bankcards.controller;

import com.example.bankcards.dto.login.JwtData;
import com.example.bankcards.dto.login.LoginRequest;
import com.example.bankcards.dto.login.LoginResponse;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.util.JwtProvider;
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
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        JwtData jwt = jwtProvider.generateToken(userDetails);
        return ResponseEntity.ok(LoginResponse.of(userDetails.getId(), jwt));
    }
}
