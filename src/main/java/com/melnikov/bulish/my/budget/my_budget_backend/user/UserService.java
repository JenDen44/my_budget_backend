package com.melnikov.bulish.my.budget.my_budget_backend.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    public UserDto findUserById(Integer id);
}