package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.TransferService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    private Authentication createTestAuthentication(Role role) {
        User user = new User(1L);
        user.setUsername("admin");
        user.setRole(role);
        UserDetailsImpl userDetails = UserDetailsImpl.of(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    private Transfer createTestTransfer() {
        Transfer transfer = new Transfer(1L);
        transfer.setAmount(new BigDecimal("500.00"));
        Card fromCard = new Card(1L);
        fromCard.setLastFourDigits("1234");
        User fromUser = new User(2L);
        fromUser.setUsername("sender");
        fromCard.setUser(fromUser);
        
        Card toCard = new Card(2L);
        toCard.setLastFourDigits("5678");
        User toUser = new User(3L);
        toUser.setUsername("receiver");
        toCard.setUser(toUser);
        
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        
        return transfer;
    }

    @Test
    void getTransfer_Success() throws Exception {
        Transfer transfer = createTestTransfer();
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(transferService.getTransfer(1L)).thenReturn(transfer);

        mockMvc.perform(get("/api/admin/transfers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void getTransfers_Success() throws Exception {
        Transfer transfer = createTestTransfer();
        Page<Transfer> transferPage = new PageImpl<>(List.of(transfer), PageRequest.of(0, 10), 1);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(transferService.getTransfers(any())).thenReturn(transferPage);

        mockMvc.perform(get("/api/admin/transfers")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void getTransfer_Forbidden() throws Exception {
        Authentication authentication = createTestAuthentication(Role.USER);

        mockMvc.perform(get("/api/admin/transfers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }
}