package com.example.bankcards.dto.card;

import com.example.bankcards.entity.impl.Card;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardResponse(Long id, String hiddenNumber, String status, LocalDate expiryDate, BigDecimal balance,
                           @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt) {
    private static final String CARD_MASK = "**** **** **** %s";

    public static CardResponse of(Card card) {
        return new CardResponse(card.getId(), CARD_MASK.formatted(card.getLastFourDigits()), card.getCardStatus().toString(), card.getExpiryDate(), card.getBalance(), card.getCreatedAt(), card.getUpdatedAt());
    }
}
