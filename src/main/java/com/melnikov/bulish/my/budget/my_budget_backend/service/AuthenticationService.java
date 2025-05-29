package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse register(AuthenticationRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshToken(String authorizationHeader);
}
