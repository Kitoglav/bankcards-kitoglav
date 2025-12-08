package com.example.bankcards.dto.admin;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.CreditCardNumber;

public record CreateCardRequest(@CreditCardNumber String cardNumber, @Min(1) Long userId) {
}
