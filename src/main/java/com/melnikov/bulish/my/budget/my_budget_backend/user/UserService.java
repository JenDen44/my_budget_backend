package com.melnikov.bulish.my.budget.my_budget_backend.user;

import org.springframework.stereotype.Service;


@Service
public interface UserService {

    public UserDto findUserById(Integer id);
}