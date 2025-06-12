package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.RefreshTokenRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.TokenType;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private AuthenticationRequest request;
    private AuthenticationResponse response;

    private RefreshTokenRequest refreshRequest;


    @BeforeEach
    void setUp() {
        request =  new AuthenticationRequest("testUser", "Password040!");
        response = new AuthenticationResponse("jwtToken", "refreshToken", TokenType.BEARER);
        refreshRequest = new RefreshTokenRequest("refreshToken");
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void register() throws Exception {
        when(authenticationService.register(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwtToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void login() throws Exception {
        when(authenticationService.login(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwtToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void refreshToken() throws Exception {
        AuthenticationResponse response = new AuthenticationResponse("newToken", "newRefreshToken", TokenType.BEARER);

        when(authenticationService.refreshToken(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }
}
