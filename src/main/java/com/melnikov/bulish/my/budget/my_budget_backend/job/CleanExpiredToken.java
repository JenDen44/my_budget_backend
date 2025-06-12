package com.melnikov.bulish.my.budget.my_budget_backend.job;

import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CleanExpiredToken {

    private final TokenRepository tokenRepo;

    public CleanExpiredToken(TokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Scheduled(fixedRateString = "${token.clean_up:6}", timeUnit = TimeUnit.HOURS)
    public void cleanAllExpiredTokens() {
        try {
           int countDeletedTokens = tokenRepo.deleteByExpiredTrueOrRevokedTrue();
           log.info("Deleted {} expired/revoked tokens", countDeletedTokens);
        } catch (Exception ex) {
            log.error("Failed to clean expired tokens", ex);
        }
    }
}
