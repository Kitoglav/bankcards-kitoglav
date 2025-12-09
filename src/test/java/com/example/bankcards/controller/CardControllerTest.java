package com.example.bankcards.controller;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

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

    private Card createTestCard() {
        Card card = new Card(1L);
        card.setCardNumberHash("hash123");
        card.setLastFourDigits("1234");
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setBalance(new BigDecimal("1000.00"));
        card.setCardStatus(CardStatus.ACTIVE);
        User user = createTestUser();
        card.setUser(user);
        return card;
    }

    @Test
    void getCard_Success() throws Exception {
        Card card = createTestCard();
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        when(userService.getUser(1L)).thenReturn(user);
        when(cardService.getCardAsOwner(1L, user)).thenReturn(card);

        mockMvc.perform(get("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hiddenNumber").value(new StringEndsWith("1234")))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void getCard_NotFound() throws Exception {
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        when(userService.getUser(1L)).thenReturn(user);
        when(cardService.getCardAsOwner(1L, user)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/cards/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void blockCard_Success() throws Exception {
        Card card = createTestCard();
        card.setCardStatus(CardStatus.BLOCKED);
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        when(userService.getUser(1L)).thenReturn(user);
        when(cardService.blockCardAsOwner(1L, user)).thenReturn(card);

        mockMvc.perform(post("/api/cards/block/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void getCards_Success() throws Exception {
        Card card = createTestCard();
        User user = createTestUser();
        Authentication authentication = createTestAuthentication(user);
        Page<Card> cardPage = new PageImpl<>(List.of(card), PageRequest.of(0, 10), 1);

        when(userService.getUser(1L)).thenReturn(user);
        when(cardService.getUserCards(any(User.class), any())).thenReturn(cardPage);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }
}