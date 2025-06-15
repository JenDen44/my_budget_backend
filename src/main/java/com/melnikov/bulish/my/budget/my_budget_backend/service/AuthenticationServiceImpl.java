package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.TokenType;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ValidationException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.GeneratedToken;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationResponse register(AuthenticationRequest request) {
        log.info("AuthenticationService.register() started");
        String userName = request.getUsername();

        if (!userService.isUsernameUnique(userName)) {
            log.error("Username is already in use {}", userName);
            throw new ValidationException("User","Username is already in use");
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("User created - ID: {}, Name: {}", user.getId(), user.getUsername());

        return assignNewTokens(user);
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

        revokeAllUserTokens(user);
        return assignNewTokens(user);
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        log.info("AuthenticationService.refreshToken() is started");

        var username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            log.error("ValidationException : The extracted username from token is null");
            throw new ValidationException("Token","Extracted username from token is null");
        }

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .filter(t -> !t.isExpired() && !t.isRevoked())
                .orElseThrow(() -> {
                    log.warn("Invalid refresh token attempt for user: {}", username);
                    return new SecurityException("Invalid or expired token");
                });

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("ResourceNotFoundException {}", username);
                    return new ResourceNotFoundException("User", username);
                });


        if (!jwtService.validateRefreshToken(refreshToken, user)) {
            log.warn("JWT validation failed for user: {}", user.getId());
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            throw new SecurityException("Token validation failed");
        }

        log.debug("User id: {} name: {} requested refresh token", user.getId(), user.getUsername());

        revokeAllUserTokens(user);
        return assignNewTokens(user);
    }

    @Override
    public void logout(String refreshToken) {
        tokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                });
    }

    private void saveUserToken(User user, GeneratedToken jwtToken, TokenType tokenType) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken.getToken())
            .expirationTime(jwtToken.getExpirationTime())
            .tokenType(tokenType)
            .expired(false)
            .revoked(false)
            .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    private AuthenticationResponse assignNewTokens(User user) {
        var newJwtToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, newRefreshToken, TokenType.BEARER);

        return AuthenticationResponse.builder()
                .accessToken(newJwtToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .tokenType(TokenType.BEARER)
                .build();
    }
}