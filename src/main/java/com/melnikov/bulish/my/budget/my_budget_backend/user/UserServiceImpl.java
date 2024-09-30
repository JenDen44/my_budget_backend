package com.melnikov.bulish.my.budget.my_budget_backend.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDto findUserById(Integer id) {
        log.debug("UserServiceImpl.findUserById() is started with parameters {} ", id);

        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));
        log.debug("User from db {} ", user);

        return new UserDto(user);
    }

    public boolean isUserNameUnique(String userName) {

       return userRepo.findByUsername(userName).isEmpty();

    }

    public User getCurrentUser() {
        Optional<User> currentUser = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = userRepo.findByUsername(currentUserName);
        }

         return currentUser.orElseThrow(() -> new UserNotFoundException("No one Authenticated user is returned"));
    }

    public User findByUserName(String username) {
       Optional<User> user = userRepo.findByUsername(username);

        return user.orElseThrow(() -> new UserNotFoundException("No one user was found"));
    }
}
