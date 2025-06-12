package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;

public interface UserService {

    boolean isUsernameUnique(String userName);

    User getCurrentUser();

    User findByUsername(String username);

}
