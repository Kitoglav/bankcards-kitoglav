package com.example.bankcards.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record CreateTransferRequest(@Min(1) Long fromCard, @Min(1) Long toCard,
                                    @Digits(integer = 15, fraction = 2) @DecimalMin("0.01") BigDecimal amount) {
}
