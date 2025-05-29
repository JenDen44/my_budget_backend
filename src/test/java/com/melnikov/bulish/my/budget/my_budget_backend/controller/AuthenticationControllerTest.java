package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationServiceImpl;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
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


    @BeforeEach
    void setUp() {
        request =  new AuthenticationRequest("testUser", "Password040!");
        response = new AuthenticationResponse("jwtToken", "refreshToken");
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void register() throws Exception {
        when(authenticationService.register(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("jwtToken"))
                .andExpect(jsonPath("$.refresh_token").value("refreshToken"));
    }

    @Test
    void login() throws Exception {
        when(authenticationService.login(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("jwtToken"))
                .andExpect(jsonPath("$.refresh_token").value("refreshToken"));
    }

    @Test
    void refreshToken() throws Exception {
        String authHeader = "Bearer oldToken";
        AuthenticationResponse response = new AuthenticationResponse("newToken", "newRefreshToken");

        when(authenticationService.refreshToken(authHeader))
                .thenReturn(response);

        mockMvc.perform(get("/refresh")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("newToken"))
                .andExpect(jsonPath("$.refresh_token").value("newRefreshToken"));
    }
}
