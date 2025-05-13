package com.melnikov.bulish.my.budget.my_budget_backend.repository;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    List<Token> findByUserIdAndExpiredFalseOrRevokedFalse(Integer userId);

    @Modifying
    @Transactional
    void deleteByExpiredTrueOrRevokedTrue();
}