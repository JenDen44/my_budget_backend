package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.AuthenticationException;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ResourceNotFoundException;
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
        mockUser = new User(1, "testUser");
    }

    @Test
    void isUserNameUniqueTrue() {
        when(userRepo.findByUsername(mockUser.getUsername())).thenReturn(Optional.empty());

        boolean result = userService.isUserNameUnique(mockUser.getUsername());
        assertThat(result).isTrue();

        verify(userRepo).findByUsername(anyString());
    }

    @Test
    void isUserNameUniqueFalse() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        boolean result = userService.isUserNameUnique(mockUser.getUsername());
        assertThat(result).isFalse();

        verify(userRepo).findByUsername(anyString());
    }

    @Test
    void getCurrentUser() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

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
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void findByUserName() {
        when(userRepo.findByUsername("existingUser")).thenReturn(Optional.of(mockUser));

        User result = userService.findByUserName("existingUser");

        assertThat(result).isEqualTo(mockUser);
        verify(userRepo).findByUsername("existingUser");
    }

    @Test
    void findByUserNameFailed() {
        when(userRepo.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUserName("unknownUser"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepo).findByUsername("unknownUser");
    }
}