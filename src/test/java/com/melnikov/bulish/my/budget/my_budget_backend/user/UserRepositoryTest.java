package com.melnikov.bulish.my.budget.my_budget_backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Rollback(value = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired UserRepository userRepo;

    @Test
    public void createUser() {
        User user = new User("test@mail.ru", "password");
        User savedUser = userRepo.save(user);

            assertThat(savedUser.getId()).isNotNull();
    }
    @Test
    public void updateUser() {
        Integer id = 1;
        User user = userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        user.setUsername("denis@mail.ru");
        User updatedUser  = userRepo.save(user);

            assertThat(updatedUser.getUsername().equals("denis@mail.ru"));

    }

    @Test
    public void getAllUsers() {
        List<User> userList = userRepo.findAll();

            assertThat(userList).isNotEmpty();
    }
    @Test
    public void findUserById() {
        Integer id = 3;
       UserNotFoundException userNotFoundExc = assertThrows(UserNotFoundException.class, () -> {
           User user = userRepo.findById(id)
                   .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));
        });

        String expectedMessage = "User with id " +  id + " is not found in DB";
        String actualMessage = userNotFoundExc.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteUser() {
        Integer id = 1;
        userRepo.deleteById(id);

        UserNotFoundException userNotFoundExc = assertThrows(UserNotFoundException.class, () -> {
            User user = userRepo.findById(id)
                    .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));
        });

        String expectedMessage = "User with id " +  id + " is not found in DB";
        String actualMessage = userNotFoundExc.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
    }
}
