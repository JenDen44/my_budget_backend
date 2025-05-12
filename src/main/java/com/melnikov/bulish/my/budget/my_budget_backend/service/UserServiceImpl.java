package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.AuthenticationException;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class UserServiceImpl {

    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isUserNameUnique(String userName) {
        return userRepo.findByUsername(userName).isEmpty();
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationException("No one Authenticated user is found");
        }

        var currentUserName = authentication.getName();

        return userRepo.findByUsername(currentUserName)
            .orElseThrow(() -> new ResourceNotFoundException("User"));
    }

    public User findByUserName(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}
