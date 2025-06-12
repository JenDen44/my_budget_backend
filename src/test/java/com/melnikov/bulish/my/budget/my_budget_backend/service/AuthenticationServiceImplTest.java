package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ValidationException;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.TokenRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenServiceImpl jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserServiceImpl userService;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    private User mockUser;
    private final String jwt = "jwt_token";
    private final String refresh = "refresh_token";

    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void setup() {
        mockUser = new User("testuser", "encodedPassword");
        mockUser.setId(1L);
        authenticationRequest = new AuthenticationRequest("testuser", "testpasswordR@$34");
    }

    @Test
    void register() {
        when(userService.isUsernameUnique(authenticationRequest.getUsername())).thenReturn(true);
        when(passwordEncoder.encode(authenticationRequest.getPassword())).thenReturn(mockUser.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn(jwt);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refresh);

        AuthenticationResponse response = authService.register(authenticationRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(jwt);
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void registerFailed() {
        when(userService.isUsernameUnique(anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.register(authenticationRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void login() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn(jwt);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refresh);

        AuthenticationResponse response = authService.login(authenticationRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(jwt);
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername(authenticationRequest.getUsername());
        verify(tokenRepository).save(any(Token.class));
    }


    @Test
    void refreshToken() {
        Token token = Token.builder().token("refresh").revoked(false).expired(false).build();

        when(jwtService.extractUsername(token.getToken())).thenReturn(mockUser.getUsername());
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));
        when(jwtService.validateRefreshToken(token.getToken(), mockUser)).thenReturn(true);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn(jwt);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refresh);

        AuthenticationResponse response = authService.refreshToken(token.getToken());

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(jwt);
        verify(tokenRepository).findByToken(token.getToken());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void refreshTokenFailed() {
        when(jwtService.extractUsername(anyString())).thenReturn(null);

        assertThatThrownBy(() -> authService.refreshToken(refresh))
                .isInstanceOf(ValidationException.class);
    }
}
