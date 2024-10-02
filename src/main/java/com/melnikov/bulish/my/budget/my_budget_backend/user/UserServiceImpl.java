package com.melnikov.bulish.my.budget.my_budget_backend.user;

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
            throw new UserNotFoundException("No one Authenticated user is returned");
        }

        var currentUserName = authentication.getName();

        return userRepo.findByUsername(currentUserName)
            .orElseThrow(() -> new UserNotFoundException("No one Authenticated user is returned"));
    }

    public User findByUserName(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("No one user was found"));
    }
}
