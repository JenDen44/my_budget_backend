package com.melnikov.bulish.my.budget.my_budget_backend.job;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class MarkExpiredTokens {

    private final TokenRepository tokenRepo;

    @Scheduled(fixedRateString = "${token.mark_expired:3}", timeUnit = TimeUnit.HOURS)
    public void markAllExpiredTokens() {
        try {
            List<Token> expiredTokens = tokenRepo.findByExpirationTimeBefore(Instant.now());
            log.info("Expired tokens count {}", expiredTokens.size());
            expiredTokens.forEach(token -> token.setExpired(true));
            tokenRepo.saveAll(expiredTokens);
            log.info("All expired tokens were marked as expired");
        } catch (Exception ex) {
            log.error("Failed to mark expired tokens", ex);
        }
    }
}
