package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse register(AuthenticationRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshToken(String authorizationHeader);

    void logout(String refreshToken);
}
