package com.example.bankcards.service;

import com.example.bankcards.dto.admin.CreateCardRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final EncryptionProvider encryptionProvider;
    private final UserService userService;

    private Card findCard(final Long id) throws EntityNotFoundException {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card with id %s not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public Card getCard(final Long id) {
        return findCard(id);
    }

    @Transactional(readOnly = true)
    public Card getCardAsOwner(final Long id, final User owner) throws EntityNotFoundException, AccessDeniedException {
        Card card = findCard(id);
        checkCardAccess(card, owner);
        return card;
    }

    private void checkCardAccess(Card card, User user) throws AccessDeniedException {
        if (!Objects.equals(card.getUser(), user)) {
            AccessDeniedException e = new AccessDeniedException("No access for this card");
            log.error("User {} tried to access card {} without ownership", user.getUsername(), card.getId(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<Card> getCards(final Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional
    public Card deleteCard(final Long cardId) {
        Card card = findCard(cardId);
        cardRepository.delete(card);
        return card;
    }

    @Transactional
    public Card blockCardAsOwner(final Long id, final User owner) throws AccessDeniedException {
        Card card = findCard(id);
        checkCardAccess(card, owner);
        if (card.getCardStatus() == CardStatus.ACTIVE) {
            card.setCardStatus(CardStatus.BLOCKED);
            return cardRepository.save(card);
        }
        AccessDeniedException e = new AccessDeniedException("Card is not activated for block operation");
        log.error("User {} tried to block non-activated card", owner.getUsername(), e);
        throw e;
    }

    @Transactional
    public Card activateCard(final Long id) {
        Card card = findCard(id);
        card.setCardStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }

    @Transactional
    public Card blockCard(final Long id) {
        Card card = findCard(id);
        card.setCardStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Card checkExpiration(final Long id) {
        Card card = getCard(id);
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setCardStatus(CardStatus.EXPIRED);
            return cardRepository.save(card);
        }
        return card;
    }

    @Transactional
    //TODO: проверка на уникальность номера карты, пока не придумал безопасное решение
    public Card createNewCard(final CreateCardRequest request) {
        String encryptedNumber = encryptionProvider.encrypt(request.cardNumber());
        Card card = new Card();
        card.setCardNumberHash(encryptedNumber);
        card.setLastFourDigits(request.cardNumber().substring(12));
        card.setBalance(BigDecimal.ZERO);
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setCardStatus(CardStatus.PENDING);
        User user = userService.getUser(request.userId());
        card.setUser(user);
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Page<Card> getUserCards(User user, Pageable pageable) {
        return cardRepository.findAllByUser(user, pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Card setBalance(Card card, BigDecimal balance) throws IllegalTransactionStateException {
        card.setBalance(balance);
        return cardRepository.save(card);
    }

    public void checkStatus(Card card) throws TransferException.CardExpired, TransferException.CardBlocked, TransferException.CardIsPending {
        switch (card.getCardStatus()) {
            case PENDING ->
                    throw new TransferException.CardExpired("Card %s is pending for activation".formatted(card.getId()));
            case BLOCKED -> throw new TransferException.CardBlocked("Card %s is blocked".formatted(card.getId()));
            case EXPIRED -> throw new TransferException.CardIsPending("Card %s is expired".formatted(card.getId()));
        }
    }

    public void checkBalance(Card card, BigDecimal amount) throws TransferException.InsufficientBalance {
        if (card.getBalance().compareTo(amount) < 0) {
            throw new TransferException.InsufficientBalance("Balance is insufficient for this transfer");
        }
    }
}
