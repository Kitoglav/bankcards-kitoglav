package com.example.bankcards.repository;


import com.example.bankcards.entity.impl.Transfer;
import com.example.bankcards.entity.impl.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findAllByUser(final User user);

    Page<Transfer> findAllByUser(final User user, Pageable pageable);
}
