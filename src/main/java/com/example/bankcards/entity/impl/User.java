package com.example.bankcards.entity.impl;

import com.example.bankcards.entity.JpaUpdatableEntity;
import com.example.bankcards.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends JpaUpdatableEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    /**
     * Не использовать нигде, кроме юнит-тестов
     * @param id
     */
    @Deprecated
    public User(Long id) {
        super(id);
    }

    public User() {
    }
}
