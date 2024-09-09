package com.melnikov.bulish.my.budget.my_budget_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

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
}
