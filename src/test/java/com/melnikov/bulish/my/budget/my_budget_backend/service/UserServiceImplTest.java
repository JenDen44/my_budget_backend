package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.AuthenticationException;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setId(1L);
    }

    @Test
    void isUserNameUniqueTrue() {
        when(userRepo.existsByUsername(mockUser.getUsername())).thenReturn(false);

        boolean result = userService.isUsernameUnique(mockUser.getUsername());

        assertThat(result).isTrue();
        verify(userRepo).existsByUsername(anyString());
    }

    @Test
    void isUserNameUniqueFalse() {
        when(userRepo.existsByUsername(mockUser.getUsername())).thenReturn(true);

        boolean result = userService.isUsernameUnique(mockUser.getUsername());

        assertThat(result).isFalse();
        verify(userRepo).existsByUsername(anyString());
    }

    @Test
    void getCurrentUser() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(mockUser.getUsername());
        when(userRepo.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));

        User result = userService.getCurrentUser();

        assertThat(result).isEqualTo(mockUser);
        verify(userRepo).findByUsername(anyString());
    }

    @Test
    void getCurrentUserFailed() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void findByUserName() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        User result = userService.findByUsername(mockUser.getUsername());

        assertThat(result).isEqualTo(mockUser);
        verify(userRepo).findByUsername(anyString());
    }
}
