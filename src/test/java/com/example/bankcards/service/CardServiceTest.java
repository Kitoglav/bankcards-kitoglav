package com.example.bankcards.service;

import com.example.bankcards.dto.admin.CreateCardRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionProvider;
import com.example.bankcards.util.properties.EncryptionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;


    private CardService cardService;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(userRepository, passwordEncoder);
        //Тестовый секрет
        EncryptionProvider encryptionProvider = new EncryptionProvider(new EncryptionProperties("XieRJhpIt3SSuUsJLCrj2C+8JR/FEJbV960JzOWvsyI="));
        cardService = new CardService(cardRepository, encryptionProvider, userService);
        testUser = new User(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);

        testCard = new Card(1L);
        testCard.setBalance(new BigDecimal("1000.00"));
        testCard.setCardStatus(CardStatus.ACTIVE);
        testCard.setUser(testUser);
    }

    @Test
    void getCardAsOwner_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        Card result = cardService.getCardAsOwner(1L, testUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getUser());
    }

    @Test
    void getCardAsOwner_NotOwner() {
        User otherUser = new User(2L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        assertThrows(RuntimeException.class, () -> cardService.getCardAsOwner(1L, otherUser));
    }

    @Test
    void blockCardAsOwner_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.blockCardAsOwner(1L, testUser);

        assertEquals(CardStatus.BLOCKED, result.getCardStatus());
    }

    @Test
    void getUserCards_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard), pageRequest, 1);

        when(cardRepository.findAllByUser(testUser, pageRequest)).thenReturn(cardPage);

        Page<Card> result = cardService.getUserCards(testUser, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(testCard, result.getContent().get(0));
    }

    @Test
    void createNewCard_Success() {
        CreateCardRequest request = new CreateCardRequest("1234567890123456", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(1L);
            return card;
        });

        Card result = cardService.createNewCard(request);
        assertNotNull(result);
        assertNotNull(result.getCardNumberHash());
        assertEquals(CardStatus.PENDING, result.getCardStatus());
        assertEquals(testUser, result.getUser());
    }
}

