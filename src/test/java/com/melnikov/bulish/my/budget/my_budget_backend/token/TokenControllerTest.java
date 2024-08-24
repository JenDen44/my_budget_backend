package com.melnikov.bulish.my.budget.my_budget_backend.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.auth.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.auth.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.auth.RefreshTokenResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenControllerTest {


    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenRepository repo;

    @Test
    public void register() throws Exception {
        String url = "/register";
        RegisterRequest request = new RegisterRequest("Mikle","test0404");

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthenticationResponse responseFromServer = objectMapper.readValue(response, AuthenticationResponse.class);

        String accessToken = responseFromServer.getAccessToken();
        Optional<Token> token = repo.findByToken(responseFromServer.getRefreshToken());

        assertThat(accessToken).isNotNull();
        assertThat(token.isPresent()).isTrue();
    }

    @Test
    public void login() throws Exception {
        String url = "/login";
        AuthenticationRequest request = new AuthenticationRequest("Mikle","test0404");

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthenticationResponse responseFromServer = objectMapper.readValue(response, AuthenticationResponse.class);

        String accessToken = responseFromServer.getAccessToken();
        Optional<Token> token = repo.findByToken(responseFromServer.getRefreshToken());

        assertThat(accessToken).isNotNull();
        assertThat(token.isPresent()).isTrue();
    }
    @Test
    public void refresh() throws Exception {
        String url = "/refresh";
        String refreshToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOYXRhc2hhIiwiaWF0IjoxNzI0NDk4MjEzLCJleHAiOjE3MjUxMDMwMTN9.aYnrgH2Jwseq73qXObAXRa54wmmuhYD_duFGlYX-8lc";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", refreshToken);

        MvcResult result = mockMvc.perform(get(url).headers(headers))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        RefreshTokenResponse responseFromServer = objectMapper.readValue(response, RefreshTokenResponse.class);
        String accessToken = responseFromServer.getAccessToken();

        assertThat(accessToken).isNotNull();
    }
}
