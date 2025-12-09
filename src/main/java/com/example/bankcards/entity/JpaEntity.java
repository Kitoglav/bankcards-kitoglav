package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class JpaEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    protected Long id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Не использовать нигде, кроме юнит-тестов
     * @param id
     */
    @Deprecated
    public JpaEntity(Long id) {
        this.id = id;
    }

    public JpaEntity() {
    }


    /**
     * Не использовать нигде, кроме юнит-тестов
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        JpaEntity jpaEntity = (JpaEntity) object;

        return id.equals(jpaEntity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
