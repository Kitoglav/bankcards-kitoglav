package com.example.bankcards.controller;

import com.example.bankcards.dto.admin.CreateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication createTestAuthentication(Role role) {
        User user = new User(1L);
        user.setUsername("admin");
        user.setRole(role);
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    private User createTestUser() {
        User user = new User(2L);
        user.setUsername("testuser");
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void createUser_Success() throws Exception {
        User user = createTestUser();
        CreateUserRequest request = new CreateUserRequest("testuser", "password");
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(userService.createNewUser(any(CreateUserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUser_Success() throws Exception {
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(userService.getUser(2L)).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        User user = createTestUser();
        Page<User> userPage = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(userService.getUsers(any())).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void deleteUser_Success() throws Exception {
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(userService.deleteUser(2L)).thenReturn(user);

        mockMvc.perform(delete("/api/admin/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void createUser_Forbidden() throws Exception {
        CreateUserRequest request = new CreateUserRequest("testuser", "password");
        Authentication authentication = createTestAuthentication(Role.USER);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }
}