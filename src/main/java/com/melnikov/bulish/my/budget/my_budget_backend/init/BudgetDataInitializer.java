package com.melnikov.bulish.my.budget.my_budget_backend.init;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.TokenType;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetDataInitializer {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PurchaseRepository purchaseRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initBudgetData() {
        if (userRepository.count() > 0) return;

        User user = userRepository.save(
                User.builder()
                        .username("testuser")
                        .password(passwordEncoder.encode("TestPassword123!"))
                        .build()
        );

        Token activeToken = Token.builder()
                .token("dummy_valid_token")
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();

        Token expiredToken = Token.builder()
                .token("dummy_expired_token")
                .expiryDate(Instant.now().minus(1, ChronoUnit.HOURS))
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(true)
                .user(user)
                .build();

        userRepository.save(user);
        tokenRepository.saveAll(List.of(activeToken, expiredToken));

        List<Purchase> purchases = List.of(
                createPurchase(Category.FOOD, 4.99, 2, LocalDate.now().minusDays(3), user),
                createPurchase(Category.FOOD, 35.50, 1, LocalDate.now().minusDays(1), user),
                createPurchase(Category.ENTERTAINMENT, 15.00, 3, LocalDate.now().minusWeeks(1), user)
        );

        purchaseRepository.saveAll(purchases);
    }

    private Purchase createPurchase(Category category, double cost, int quantity,
                                    LocalDate date, User user) {
        return Purchase.builder()
                .category(category)
                .cost(cost)
                .quantity(quantity)
                .purchaseDate(date)
                .user(user)
                .build();
    }
}
