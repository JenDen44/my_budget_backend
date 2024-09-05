package com.melnikov.bulish.my.budget.my_budget_backend.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) throw new UserNotFoundException("No one user was found in DB");

        List<UserDto> userDtoList = users.stream().map(user -> new UserDto(user)).toList();

        return userDtoList;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {

        if (!isUserNameUnique(userDto.getUsername())) throw new UserValidationException("The username is already in use");
        if (!isPasswordValid(userDto.getPassword())) throw new UserValidationException("The password is not strong enough");

        return new UserDto(userRepo.save(new User(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        if (!isUserNameUnique(userDto.getUsername())) throw new UserValidationException("The username is already in use");
        if (!isPasswordValid(userDto.getPassword())) throw new UserValidationException("The password is not strong enough");

        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        return new UserDto(userRepo.save(user));
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        userRepo.deleteById(id);

    }
    public boolean isUserNameUnique(String userName) {

       return userRepo.findByUsername(userName).isEmpty();

    }

    public boolean isPasswordValid(String password) {
        String validation = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(validation, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
