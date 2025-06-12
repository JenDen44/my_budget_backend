package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.WebSocketException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.WebSocketPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketSessionServiceTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketSessionService sessionService = new WebSocketSessionService(objectMapper);
    @Mock
    private WebSocketSession session1;
    @Mock
    private WebSocketSession session2;
    @Mock
    private WebSocketSession session3;
    @Mock
    private WebSocketSession session4;

    private final Long userId = 1L;

    @Test
    void registerSessionSuccess() throws WebSocketException {
        registerTestSession();
        assertEquals(3, sessionService.getSessionCountForUser(userId));
    }

    @Test
    void registerSessionThrowsException() throws WebSocketException {
        registerTestSession();
        assertThrows(WebSocketException.class,
                () -> sessionService.registerSession(session4, userId));
    }

    @Test
    void removeSession() throws WebSocketException {
        registerTestSession();

        sessionService.removeSession("sess1");

        assertEquals(2, sessionService.getSessionCountForUser(userId));
        assertFalse(sessionService.isAuthenticated("sess1"));
    }

    @Test
    void sendMessage() throws IOException, WebSocketException {
        registerTestSession();

        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        when(session3.isOpen()).thenReturn(true);

        WebSocketPayload<String> payload = new WebSocketPayload<>("test", objectMapper);

        sessionService.sendMessage(userId, payload);

        verify(session1).sendMessage(any());
        verify(session2).sendMessage(any());
        verify(session3).sendMessage(any());
    }

    private void registerTestSession() throws WebSocketException {
        when(session1.getId()).thenReturn("sess1");
        when(session2.getId()).thenReturn("sess2");
        when(session3.getId()).thenReturn("sess3");

        sessionService.registerSession(session1, userId);
        sessionService.registerSession(session2, userId);
        sessionService.registerSession(session3, userId);
    }
}