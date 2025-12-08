package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/transfers")
@RequiredArgsConstructor
@Tag(name = "Администрирование переводов", description = "API для администраторов по управлению переводами")

public class AdminTransferController {
    private final TransferService transferService;

    @Operation(summary = "Получить перевод по ID", description = "Возвращает информацию о переводе по его идентификатору")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Перевод найден"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "404", description = "Перевод не найден")})
    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransfer(@Parameter(description = "ID перевода", required = true, example = "1") @PathVariable @Min(1) Long transferId) {
        Transfer transfer = transferService.getTransfer(transferId);
        return ResponseEntity.ok(TransferResponse.of(transfer));
    }

    @Operation(summary = "Получить все переводы", description = "Возвращает список всех переводов в системе с пагинацией")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Успешно получен список переводов"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав")})
    @GetMapping
    public ResponseEntity<Page<TransferResponse>> getTransfers(@Parameter(description = "Параметры пагинации") final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Transfer> transfers = transferService.getTransfers(pageable);
        return ResponseEntity.ok(transfers.map(TransferResponse::of));
    }
}