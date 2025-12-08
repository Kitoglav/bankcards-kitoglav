package com.example.bankcards.service;

import com.example.bankcards.dto.transfer.CreateTransferRequest;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final CardService cardService;

    private Transfer findTransfer(Long id) {
        return transferRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transfer with id %s not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public Transfer getTransfer(Long id) {
        return findTransfer(id);
    }

    @Transactional(readOnly = true)
    public Page<Transfer> getTransfers(Pageable pageable) {
        return transferRepository.findAll(pageable);
    }

    @Transactional
    public Transfer createNewTransfer(User user, CreateTransferRequest request) {
        try {
            cardService.checkExpiration(request.fromCard());
            cardService.checkExpiration(request.toCard());

            Card fromCard = cardService.getCardAsOwner(request.fromCard(), user);
            Card toCard = cardService.getCardAsOwner(request.toCard(), user);

            cardService.checkStatus(fromCard);
            cardService.checkStatus(toCard);

            if (Objects.equals(fromCard, toCard)) {
                TransferException e = new TransferException("Can't transfer to same card");
                log.error("User {} tried to transfer to same card", user.getUsername(), e);
                throw e;
            }
            cardService.checkBalance(fromCard, request.amount());

            Transfer transfer = new Transfer();
            transfer.setUser(user);
            transfer.setFromCard(fromCard);
            transfer.setToCard(toCard);
            transfer.setAmount(request.amount());

            cardService.setBalance(fromCard, fromCard.getBalance().subtract(request.amount()));
            cardService.setBalance(toCard, toCard.getBalance().add(request.amount()));

            return transferRepository.save(transfer);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("Transfer is possible only across your cards", e);
        }
    }
}
