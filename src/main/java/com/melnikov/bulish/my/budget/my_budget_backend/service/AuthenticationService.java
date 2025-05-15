package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.exception.*;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.TokenType;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    public AuthenticationResponse register(AuthenticationRequest request) {
        log.info("AuthenticationService.register() started");
        String userName = request.getUsername();

        if (!userService.isUserNameUnique(userName)) {
            log.error("Username is already in use {}", userName);
            throw new ValidationException("User","Username is already in use");
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        log.info("User is created id {}, name {}", user.getId(), user.getUsername());
        var savedUser = userRepository.save(user);

        return assignNewTokens(user, false);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        log.info("AuthenticationService.login() started");
        String userName = request.getUsername();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userName, request.getPassword())
        );

        var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> {
                log.error("ResourceNotFoundException {}", userName);
                return new ResourceNotFoundException("User", userName);
            });

        return assignNewTokens(user, true);
    }

    private void saveUserToken(User user, String jwtToken, TokenType tokenType) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(tokenType)
            .expired(false)
            .revoked(false)
            .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findByUserIdAndExpiredFalseOrRevokedFalse(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(String authorizationHeader) {
        log.info("AuthenticationService.refreshToken() is started");

        var refreshToken = jwtService.resolveToken(authorizationHeader);
        var userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            log.error("ValidationException : The extracted userEmail from token is null");
            throw new ValidationException("Token","Extracted email from token is null");
        }

        tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("ResourceNotFoundException {}", userEmail);
                    return new ResourceNotFoundException("User", userEmail);
                });


        var user = this.userRepository.findByUsername(userEmail)
                .orElseThrow(() -> {
                    log.error("ResourceNotFoundException {}", userEmail);
                    return new ResourceNotFoundException("User", userEmail);
                });

        log.debug("current user requested refresh id {}, name {}", user.getId(), user.getUsername());

        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.error("ValidationException : The token is not valid");
            throw new ValidationException("Token","is not valid");
        }

       return assignNewTokens(user, true);
    }

    private AuthenticationResponse assignNewTokens(User user, boolean revoke) {
        var newJwtToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        if (revoke) revokeAllUserTokens(user);
        saveUserToken(user, newRefreshToken, TokenType.BEARER);

        return AuthenticationResponse.builder()
                .accessToken(newJwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}