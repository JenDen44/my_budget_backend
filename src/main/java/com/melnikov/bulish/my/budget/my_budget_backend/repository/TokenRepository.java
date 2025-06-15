package com.melnikov.bulish.my.budget.my_budget_backend.repository;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query("SELECT t FROM Token t WHERE t.user.id = ?1 AND (t.expired = false AND t.revoked = false)")
    List<Token> findValidTokensByUser(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expired = true OR t.revoked = true")
    int deleteByExpiredTrueOrRevokedTrue();

    @Query("SELECT t FROM Token t WHERE t.expirationTime < :now")
    List<Token> findByExpirationTimeBefore(@Param("now") Instant now);

}