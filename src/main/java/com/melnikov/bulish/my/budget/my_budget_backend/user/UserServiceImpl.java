package com.melnikov.bulish.my.budget.my_budget_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto findUserById(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = (List<User>) userRepo.findAll();
        List<UserDto> userDtoList = users.stream().map(user -> userMapper.toDto(user)).toList();

        return userDtoList;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User userToDB = userMapper.toEntity(userDto);
        User userSavedToDB = userRepo.save(userToDB);

        return userMapper.toDto(userSavedToDB);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        User updateUser = userRepo.save(userMapper.toEntity(userDto));

        return userMapper.toDto(updateUser);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        userRepo.deleteById(id);

    }
}
