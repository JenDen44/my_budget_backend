package com.melnikov.bulish.my.budget.my_budget_backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthSocketResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.WebSocketException;
import com.melnikov.bulish.my.budget.my_budget_backend.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.concurrent.*;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private static final long AUTH_TIMEOUT_MS = 30_000;
    private final WebSocketSessionService sessionService;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;

    public WebSocketHandler(WebSocketSessionService sessionService, UserServiceImpl userService, JwtTokenService jwtTokenService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.scheduler = Executors.newScheduledThreadPool(4);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        scheduler.schedule(() -> {
            if (session.isOpen() && !sessionService.isAuthenticated(session.getId())) {
                try {
                    session.close(CloseStatus.POLICY_VIOLATION.withReason("Authentication timeout"));
                } catch (IOException ex) {
                    log.warn("Error closing unauthenticated session: {}", ex.getMessage());
                }
            }
        }, AUTH_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            var token = message.getPayload();

            if (token.isBlank() || !jwtTokenService.isTokenValid(token)) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
                return;
            }

            var username = jwtTokenService.extractUsername(token);

            if (username == null) {
                session.close();
                return;
            }

            var user = userService.findByUsername(username);

            if (user == null) {
                session.close();
                return;
            }

            sessionService.registerSession(session, user.getId());

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    new AuthSocketResponse("AUTH_SUCCESS", user.getId())
            )));

        } catch (Exception | WebSocketException e) {
        log.error("Authentication error: {}", e.getMessage());
        session.close(CloseStatus.NOT_ACCEPTABLE.withReason(e.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Connection closed: {} status = {}", session, status);
        sessionService.removeSession(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) {
        log.error("Transport error for session {}: {}", session.getId(), throwable.getMessage());

        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException ex) {
            log.warn("Error closing broken session: {}", ex.getMessage());
        }
    }
}
