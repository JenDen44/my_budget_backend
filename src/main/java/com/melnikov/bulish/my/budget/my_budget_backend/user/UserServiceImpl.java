package com.melnikov.bulish.my.budget.my_budget_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
