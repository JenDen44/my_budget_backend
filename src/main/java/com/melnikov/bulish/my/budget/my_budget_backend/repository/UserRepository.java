package com.melnikov.bulish.my.budget.my_budget_backend.repository;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String userName);

    Optional<User> findByTokens(Token token);
}