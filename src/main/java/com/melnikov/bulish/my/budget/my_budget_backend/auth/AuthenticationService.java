package com.melnikov.bulish.my.budget.my_budget_backend.auth;

import com.melnikov.bulish.my.budget.my_budget_backend.token.*;
import com.melnikov.bulish.my.budget.my_budget_backend.user.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
        log.debug("AuthenticationService.register() started");

        if (!userService.isUserNameUnique(request.getUsername()))
            throw new UserValidationException("The username is already in use");

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        log.debug("User is created {} ", user);

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, refreshToken);
        log.debug("refresh and jwtToken are generated and set to the user");

        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        log.debug("AuthenticationService.login() started");

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() ->
                new UserNotFoundException("User with username " + request.getUsername() + " is not found in DB")
            );
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, refreshToken);
        log.debug("current user {} ", user);
        log.debug("refreshToken and jwtToken was generated and set to the current user");

         return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        log.debug("AuthenticationService.refreshToken() is started");

        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            log.error("TokenValidationException : Header doesn't contain correct data for token");
            throw new TokenValidationException("Header doesn't contain correct data for token");
        }

        var refreshToken = authHeader.substring(7);
        var userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            log.error("TokenValidationException : The extracted userEmail from token is null");
            throw new TokenValidationException("The extracted userEmail from token is null");
        }

        tokenRepository.findByToken(refreshToken).
                orElseThrow(() -> new TokenNotFoundException("Token not found in DB"));


        var user = this.userRepository.findByUsername(userEmail)
            .orElseThrow(() -> new UserNotFoundException("User not found in DB by username/email " + userEmail));

        log.debug("current user requested refresh {} ", user);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.error("TokenValidationException : The token is not valid");
            throw new TokenValidationException("The token is not valid");
        }

        var newJwtToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, newRefreshToken);
        log.debug("refreshToken and jwtToken was generated and set to the current user");

        return AuthenticationResponse.builder()
            .accessToken(newJwtToken)
            .refreshToken(newRefreshToken)
            .build();
    }
}