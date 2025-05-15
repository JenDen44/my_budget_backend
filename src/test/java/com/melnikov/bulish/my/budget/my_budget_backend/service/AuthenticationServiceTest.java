package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Token;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ValidationException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.model.AuthenticationResponse;
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
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserServiceImpl userService;

    @InjectMocks
    private AuthenticationService authService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");
    }

    @Test
    void register() {
        AuthenticationRequest request = new AuthenticationRequest("newUser", "pass");
        when(userService.isUserNameUnique(request.getUsername())).thenReturn(true);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwtToken");
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void registerFailed() {
        AuthenticationRequest request = new AuthenticationRequest("existingUser", "pass");
        when(userService.isUserNameUnique("existingUser")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void login() {
        AuthenticationRequest request = new AuthenticationRequest("user", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");

        AuthenticationResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt");
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername(request.getUsername());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void loginFailed() {
        AuthenticationRequest request = new AuthenticationRequest("unknown", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void refreshToken() {
        Token token = new Token();
        token.setToken("someToken");
        String authHeader = "Bearer " + token.getToken();
        when(jwtService.resolveToken(authHeader)).thenReturn(token.getToken());
        when(jwtService.extractUsername(token.getToken())).thenReturn("user");
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("newJwt");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("newRefresh");

        AuthenticationResponse response = authService.refreshToken(authHeader);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("newJwt");
        verify(tokenRepository).findByToken(token.getToken());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void refreshTokenFailed() {
        String authHeader = "Bearer invalidToken";
        when(jwtService.resolveToken(authHeader)).thenReturn("invalidToken");
        when(jwtService.extractUsername("invalidToken")).thenReturn(null);

        assertThatThrownBy(() -> authService.refreshToken(authHeader))
                .isInstanceOf(ValidationException.class);
    }
}