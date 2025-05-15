package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;

    private static final String URL_REGISTER = "/register";
    private static final String URL_LOGIN = "/login";
    private static final String URL_REFRESH = "/refresh";

    private static AuthenticationRequest requestTestUser;

    private static AuthenticationResponse responseTestUser;

    @BeforeEach
    public void setup() throws Exception {
        createAndRegisterTestUser();
    }

    @AfterEach
    public void cleanUp() throws Exception {
        if (responseTestUser != null) {
            Optional<Token> token = tokenRepo.findByToken(responseTestUser.getAccessToken());
            token.ifPresent(value -> tokenRepo.delete(value));
        }
        if (requestTestUser != null) {
            Optional<User> user = userRepo.findByUsername(requestTestUser.getUsername());
            user.ifPresent(value -> userRepo.delete(value));
        }

        requestTestUser = null;
        responseTestUser = null;
    }

    private void createAndRegisterTestUser() throws Exception {
        var request = new AuthenticationRequest(generateRandomString(8, false), generateRandomString(12, true));

        var result = mockMvc.perform(post(URL_REGISTER)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        requestTestUser = request;
        responseTestUser = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);;
    }

    public String generateRandomString(int length, boolean password) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        if (password) sb.append("R@$34");

        return sb.toString();
    }

    @Test
    public void register() throws Exception {
        var request = new AuthenticationRequest(generateRandomString(8, false), generateRandomString(12, true));
        var result = mockMvc.perform(post(URL_REGISTER)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var responseFromServer = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);
        var accessToken = responseFromServer.getAccessToken();
        var token = tokenRepo.findByToken(responseFromServer.getRefreshToken());
        var user = userRepo.findByTokens(token.orElseThrow());

        assertThat(accessToken).isNotNull();
        assertThat(user).isNotNull();
    }

    @Test
    public void registerFailed() throws Exception {
        mockMvc.perform(post(URL_REGISTER)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestTestUser))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.errorMessage").value("User Username is already in use"));
    }

    @Test
    public void login() throws Exception {
        var result = mockMvc.perform(post(URL_LOGIN)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(requestTestUser))
            .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var response = result.getResponse().getContentAsString();
        var responseFromServer = objectMapper.readValue(response, AuthenticationResponse.class);
        var accessToken = responseFromServer.getAccessToken();
        var token = tokenRepo.findByToken(responseFromServer.getRefreshToken());

        assertThat(accessToken).isNotNull();
        assertThat(token.isPresent()).isTrue();
    }

    @Test
    public void refresh() throws Exception {
        var headers = new HttpHeaders();
        headers.add("Authorization", responseTestUser.getRefreshToken());

        var result = mockMvc.perform(get(URL_REFRESH).headers(headers))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        var response = result.getResponse().getContentAsString();
        var responseFromServer = objectMapper.readValue(response, AuthenticationResponse.class);
        var accessToken = responseFromServer.getAccessToken();

        assertThat(accessToken).isNotNull();
    }
}
