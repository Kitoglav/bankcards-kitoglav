package com.example.bankcards.dto.transfer;

import com.example.bankcards.entity.impl.Transfer;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(Long id, Long fromCard, Long toCard, BigDecimal amount, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt) {
    public static TransferResponse of(Transfer transfer) {
        return new TransferResponse(transfer.getId(), transfer.getFromCard().getId(), transfer.getToCard().getId(), transfer.getAmount(), transfer.getCreatedAt());
    }
}
