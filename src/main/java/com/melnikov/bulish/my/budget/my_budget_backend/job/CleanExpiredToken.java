package com.melnikov.bulish.my.budget.my_budget_backend.job;

import com.melnikov.bulish.my.budget.my_budget_backend.token.TokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanExpiredToken {

    private final TokenRepository tokenRepo;

    public CleanExpiredToken(TokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Scheduled(fixedRateString = "${token.clean_up}")
    public void cleanAllExpiredTokens() {
        tokenRepo.cleanAllExpiredToken();
    }
}
