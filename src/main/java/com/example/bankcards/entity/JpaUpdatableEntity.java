package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class JpaUpdatableEntity extends JpaEntity {
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    /**
     * Не использовать нигде, кроме юнит-тестов
     * @param id
     */
    @Deprecated
    public JpaUpdatableEntity(Long id) {
        super(id);
    }

    public JpaUpdatableEntity() {
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
