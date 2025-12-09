package com.example.bankcards.controller;

import com.example.bankcards.dto.transfer.CreateTransferRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication createTestAuthentication(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    private User createTestUser() {
        User user = new User(1L);
        user.setUsername("testuser");
        user.setRole(Role.USER);
        return user;
    }

    private Transfer createTestTransfer() {
        Transfer transfer = new Transfer(1L);
        transfer.setAmount(new BigDecimal("500.00"));
        
        Card fromCard = new Card(1L);
        fromCard.setLastFourDigits("1234");
        
        Card toCard = new Card(2L);
        toCard.setLastFourDigits("5678");
        
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        
        return transfer;
    }

    @Test
    void createTransfer_Success() throws Exception {
        Transfer transfer = createTestTransfer();
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("500.00"));

        when(userService.getUser(1L)).thenReturn(user);
        when(transferService.createNewTransfer(any(User.class), any(CreateTransferRequest.class)))
                .thenReturn(transfer);

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void createTransfer_BadRequest() throws Exception {
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("0.00"));

        when(userService.getUser(1L)).thenReturn(user);
        when(transferService.createNewTransfer(any(User.class), any(CreateTransferRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid amount"));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransfer_Unauthorized() throws Exception {
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("500.00"));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}