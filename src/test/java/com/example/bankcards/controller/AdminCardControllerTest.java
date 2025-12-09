package com.example.bankcards.controller;

import com.example.bankcards.dto.admin.CreateCardRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringEndsWith;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

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

    private Card createTestCard() {
        Card card = new Card(1L);
        card.setCardNumberHash("hash123");
        card.setLastFourDigits("1234");
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setBalance(new BigDecimal("1000.00"));
        card.setCardStatus(CardStatus.ACTIVE);
        User user = new User(2L);
        user.setUsername("cardowner");
        card.setUser(user);
        return card;
    }

    @Test
    void createCard_Success() throws Exception {
        Card card = createTestCard();
        CreateCardRequest request = new CreateCardRequest("1634123412341234", 1L);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.createNewCard(any(CreateCardRequest.class))).thenReturn(card);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hiddenNumber").value(new StringEndsWith("1234")));
    }

    @Test
    void createCard_Forbidden() throws Exception {
        CreateCardRequest request = new CreateCardRequest("1634123412341234", 1L);
        Authentication authentication = createTestAuthentication(Role.USER);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCard_Success() throws Exception {
        Card card = createTestCard();
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.getCard(1L)).thenReturn(card);

        mockMvc.perform(get("/api/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hiddenNumber").value("**** **** **** 1234"));
    }

    @Test
    void getAllCards_Success() throws Exception {
        Card card = createTestCard();
        Page<Card> cardPage = new PageImpl<>(List.of(card), PageRequest.of(0, 10), 1);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.getCards(any())).thenReturn(cardPage);

        mockMvc.perform(get("/api/admin/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void deleteCard_Success() throws Exception {
        Card card = createTestCard();
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.deleteCard(1L)).thenReturn(card);

        mockMvc.perform(delete("/api/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void activateCard_Success() throws Exception {
        Card card = createTestCard();
        card.setCardStatus(CardStatus.ACTIVE);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.activateCard(1L)).thenReturn(card);

        mockMvc.perform(post("/api/admin/cards/activate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void blockCard_Success() throws Exception {
        Card card = createTestCard();
        card.setCardStatus(CardStatus.BLOCKED);
        Authentication authentication = createTestAuthentication(Role.ADMIN);

        when(cardService.blockCard(1L)).thenReturn(card);

        mockMvc.perform(post("/api/admin/cards/block/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
}