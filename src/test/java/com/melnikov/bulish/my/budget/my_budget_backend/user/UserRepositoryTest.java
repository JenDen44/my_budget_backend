package com.melnikov.bulish.my.budget.my_budget_backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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
        var user = new User("test@mail.ru", "password");
        var savedUser = userRepo.save(user);

        assertThat(savedUser.getId()).isNotNull();
    }
    @Test
    public void updateUser() {
        var id = 1;
        var user = userRepo.findById(id)
            .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));

        user.setUsername("denis@mail.ru");

        var updatedUser = userRepo.save(user);

        assertThat(updatedUser.getUsername().equals("denis@mail.ru"));
    }

    @Test
    public void getAllUsers() {
        var userList = userRepo.findAll();

        assertThat(userList).isNotEmpty();
    }

    @Test
    public void findUserById() {
        var id = 3;
        var userNotFoundExc = assertThrows(UserNotFoundException.class, () -> {
           userRepo.findById(id)
               .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));
        });
        var expectedMessage = "User with id " +  id + " is not found in DB";
        var actualMessage = userNotFoundExc.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteUser() {
        var id = 1;

        userRepo.deleteById(id);

        var userNotFoundExc = assertThrows(UserNotFoundException.class, () -> {
            userRepo.findById(id)
                .orElseThrow (() -> new UserNotFoundException("User with id " + id + " is not found in DB"));
        });
        var expectedMessage = "User with id " +  id + " is not found in DB";
        var actualMessage = userNotFoundExc.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
