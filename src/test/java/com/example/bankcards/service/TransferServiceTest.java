package com.example.bankcards.service;

import com.example.bankcards.dto.transfer.CreateTransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    private TransferService transferService;

    private User testUser;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        CardService cardService = new CardService(cardRepository, null, null);
        transferService = new TransferService(transferRepository, cardService);
        testUser = new User();
        testUser.setId(1L);

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setCardStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.now().plusYears(5));
        fromCard.setUser(testUser);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(new BigDecimal("500.00"));
        toCard.setExpiryDate(LocalDate.now().plusYears(5));
        toCard.setCardStatus(CardStatus.ACTIVE);
        toCard.setUser(testUser);
    }

    @Test
    void createNewTransfer_Success() {
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("100.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setId(1L);
            return transfer;
        });

        Transfer result = transferService.createNewTransfer(testUser, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(fromCard, result.getFromCard());
        assertEquals(toCard, result.getToCard());
        assertEquals(testUser, result.getUser());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
        assertEquals(new BigDecimal("900.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("600.00"), toCard.getBalance());
    }

    @Test
    void createNewTransfer_InsufficientFunds() {
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("1500.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(RuntimeException.class, () -> transferService.createNewTransfer(testUser, request));
    }

    @Test
    void createNewTransfer_BlockedCard() {
        fromCard.setCardStatus(CardStatus.BLOCKED);
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("100.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));

        assertThrows(RuntimeException.class, () -> transferService.createNewTransfer(testUser, request));
    }
}
