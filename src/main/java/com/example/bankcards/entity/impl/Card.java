package com.example.bankcards.entity.impl;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.JpaUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card extends JpaUpdatableEntity {

    @Column(name = "card_number_hash", nullable = false, unique = true)
    private String cardNumberHash;

    @Column(name = "last_four_digits", nullable = false, length = 4)
    private String lastFourDigits;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "card_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Card(Long id) {
        super(id);
    }

    public Card() {
    }
}
