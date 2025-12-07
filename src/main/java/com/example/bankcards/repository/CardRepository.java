package com.example.bankcards.repository;

import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByUser(final User owner);

    Page<Card> findAllByUser(final User owner, final Pageable pageable);
}
