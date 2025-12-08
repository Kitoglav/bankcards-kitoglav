package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.service.TransferService;
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
public class AdminTransferController {
    private final TransferService transferService;

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransfer(@PathVariable @Min(1) Long transferId) {
        Transfer transfer = transferService.getTransfer(transferId);
        return ResponseEntity.ok(TransferResponse.of(transfer));
    }

    @GetMapping
    public ResponseEntity<Page<TransferResponse>> getTransfers(final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Transfer> transfers = transferService.getTransfers(pageable);
        return ResponseEntity.ok(transfers.map(TransferResponse::of));
    }

}
