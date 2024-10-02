package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;

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
    @WithMockUser(username = "test", password = "test")
    public void findAllUsers() throws Exception {
        var url = "/users";
        var result = mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        var jsonResponse = result.getResponse().getContentAsString();
        var users = objectMapper.readValue(jsonResponse, User[].class);

        assertThat(users).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void createUser() throws  Exception {
        var url = "/users";
        var userDto = new UserDto("user@yandex.ru","1273пупв");
        var result = mockMvc.perform(
                post(url).contentType("application/json")
                    .content(objectMapper.writeValueAsString(userDto))
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        var response = result.getResponse().getContentAsString();
        var userFromResponse = objectMapper.readValue(response, User.class);
        var findById = userRepo.findById(userFromResponse.getId());

        assertThat(findById.isPresent());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void updateUser() throws JsonProcessingException, Exception {
        var userId = 2;
        var url = "/users/" + userId;
        var userDto = new UserDto("changedUser@yandex.ru","1273пупв");
        var result = mockMvc.perform(
                put(url).contentType("application/json")
                    .content(objectMapper.writeValueAsString(userDto))
                    .with(csrf())
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        var response = result.getResponse().getContentAsString();
        var userFromResponse = objectMapper.readValue(response, User.class);
        var findById = userRepo.findById(userId);

        assertThat(findById.isPresent());
        assertThat(userFromResponse.getUsername().equals("changedUser@yandex.ru"));
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void deleteUser() throws Exception {
        var userId = 2;
        var url = "/users/" + userId;

        mockMvc.perform(delete(url)).andExpect(status().isOk());

        var findById = userRepo.findById(userId);

        assertThat(findById).isNotPresent();
    }
}

