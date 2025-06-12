package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.AuthenticationException;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    @Override
    public boolean isUsernameUnique(String username) {
        return !userRepo.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.error("AuthenticationException No authenticated user is found");
            throw new AuthenticationException("No authenticated user is found");
        }
        var currentUserName = authentication.getName();

        if (currentUserName == null || currentUserName.isBlank()) {
            log.error("Authenticated user has empty username");
            throw new AuthenticationException("Invalid username in authentication");
        }

        return findByUsername(currentUserName);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        log.debug("Fetching user: {}", username);
        return userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new ResourceNotFoundException("User", username);
                });
    }
}
