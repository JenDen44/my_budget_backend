package com.melnikov.bulish.my.budget.my_budget_backend.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    public UserDto findUserById(Integer id);

    public List<UserDto> findAllUsers();

    public UserDto saveUser(UserDto userDto);

    public UserDto updateUser(UserDto userDto, Integer id);

    public void deleteUser(Integer id);
}