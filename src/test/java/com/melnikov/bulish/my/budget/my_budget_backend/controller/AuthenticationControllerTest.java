package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private AuthenticationService service;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest createSampleRequest() {
        return new AuthenticationRequest("testuser", "testpasswordR@$34");
    }

    private AuthenticationResponse createSampleResponse() {
        return new AuthenticationResponse("access-token-sample", "refresh-token-sample");
    }

    @Test
    public void register() throws Exception {
        var request = createSampleRequest();
        var response = createSampleResponse();

        when(service.register(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(response.getAccessToken()))
                .andExpect(jsonPath("$.refresh_token").value(response.getRefreshToken()));
    }

    @Test
    public void login() throws Exception {
        var request = createSampleRequest();
        var response = createSampleResponse();

        when(service.login(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(response.getAccessToken()))
                .andExpect(jsonPath("$.refresh_token").value(response.getRefreshToken()));
    }

    @Test
    public void refreshToken() throws Exception {
        String authHeader = "Bearer mockRefreshToken123";

        AuthenticationResponse response = createSampleResponse();
        when(service.refreshToken(authHeader)).thenReturn(response);

        mockMvc.perform(get("/refresh")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(response.getAccessToken()))
                .andExpect(jsonPath("$.refresh_token").value(response.getRefreshToken()));
    }
}
