package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.AuthenticationException;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public boolean isUserNameUnique(String userName) {
        return userRepo.findByUsername(userName).isEmpty();
    }

    @Override
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.error("AuthenticationException No authenticated user is found");
            throw new AuthenticationException("No authenticated user is found");
        }
        var currentUserName = authentication.getName();
        log.info("Retrieving current user with username {}", currentUserName);

        return userRepo.findByUsername(currentUserName).orElseThrow(() -> {
            log.error("ResourceNotFoundException by userName {}", currentUserName);
            return new ResourceNotFoundException("User", currentUserName);
        });
    }

    @Override
    public User findByUserName(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}
