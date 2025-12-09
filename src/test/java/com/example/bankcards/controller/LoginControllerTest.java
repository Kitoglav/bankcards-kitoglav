package com.example.bankcards.controller;

import com.example.bankcards.dto.login.JwtData;
import com.example.bankcards.dto.login.LoginRequest;
import com.example.bankcards.dto.login.LoginResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtProvider jwtProvider;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private String getPassword() {
        return "testPassword123";
    }
    private UserDetailsImpl createUserDetails() {
        User user = createTestUser();
        return UserDetailsImpl.of(user);
    }
    private User createTestUser() {
        User user = new User(1L);
        user.setUsername("testuser");
        user.setRole(Role.USER);
        return user;
    }
    @BeforeEach
    void setUp() {
        User user = createTestUser();
        userRepository.save(user);
    }
    @Test
    void login_Success() throws Exception {
        UserDetailsImpl userDetails = createUserDetails();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        JwtData jwtData = new JwtData("test-token", new Date(), new Date());
        LoginRequest request = new LoginRequest("testuser", getPassword());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtProvider.generateToken(userDetails)).thenReturn(jwtData);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_EmptyFields() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}