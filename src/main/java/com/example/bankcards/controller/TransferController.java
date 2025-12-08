package com.example.bankcards.controller;

import com.example.bankcards.dto.transfer.CreateTransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Переводы", description = "Управление денежными переводами")

public class TransferController {
    private final TransferService transferService;
    private final UserService userService;

    @Operation(summary = "Создать перевод", description = "Создает новый денежный перевод между картами")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Перевод создан", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponse.class))), @ApiResponse(responseCode = "400", description = "Неверные данные перевода"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно средств или доступ запрещен"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(@Parameter(hidden = true) final Authentication authentication, @Parameter(description = "Данные для создания перевода", required = true) @RequestBody @Validated CreateTransferRequest request) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Transfer transfer = transferService.createNewTransfer(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransferResponse.of(transfer));
        });
    }
}