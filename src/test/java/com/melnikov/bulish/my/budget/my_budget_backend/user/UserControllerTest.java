package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Test
    public void findAllUsers() throws Exception {
        String url = "/users";

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        User[] users = objectMapper.readValue(jsonResponse, User[].class);

             assertThat(users).hasSizeGreaterThan(0);
    }

    @Test
    public void createUser() throws  Exception {
        String url = "/users";
        UserDto userDto = new UserDto("user@yandex.ru","1273пупв");

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        User userFromResponse = objectMapper.readValue(response, User.class);
        Optional<User> findById = userRepo.findById(userFromResponse.getId());

             assertThat(findById.isPresent());

    }

    @Test
    public void updateUser() throws JsonProcessingException, Exception {
        Integer userId = 2;
        String url = "/users/" + userId;

        UserDto userDto = new UserDto("changedUser@yandex.ru","1273пупв");

        MvcResult result = mockMvc.perform(put(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        User userFromResponse = objectMapper.readValue(response, User.class);

        Optional<User> findById = userRepo.findById(userId);

            assertThat(findById.isPresent());
            assertThat(userFromResponse.getUsername().equals("changedUser@yandex.ru"));

    }

    @Test
    public void deleteUser() throws Exception {
        Integer userId = 2;
        String url = "/users/" + userId;

        mockMvc.perform(delete(url)).andExpect(status().isOk());
        Optional<User> findById = userRepo.findById(userId);

            assertThat(findById).isNotPresent();
    }
}

