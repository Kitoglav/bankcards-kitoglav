package com.example.bankcards.controller;

import com.example.bankcards.dto.transfer.CreateTransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
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
public class TransferController {
    private final TransferService transferService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(final Authentication authentication, @RequestBody @Validated CreateTransferRequest request) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Transfer transfer = transferService.createNewTransfer(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransferResponse.of(transfer));
        });
    }
}
