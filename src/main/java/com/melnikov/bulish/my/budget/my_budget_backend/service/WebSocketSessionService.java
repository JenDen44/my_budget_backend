package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.WebSocketException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.WebSocketPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionService {
    private static final int MAX_SESSIONS_PER_USER = 3;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToUserId = new ConcurrentHashMap<>();
    private final Map<Long, AtomicInteger> userSessionCounts = new ConcurrentHashMap<>();

    private final ObjectMapper mapper;

    public void registerSession(WebSocketSession session, Long userId) throws WebSocketException {
        String sessionId = session.getId();

        if (userSessionCounts.computeIfAbsent(userId, k ->
                new AtomicInteger(0)).incrementAndGet() > MAX_SESSIONS_PER_USER) {
            userSessionCounts.get(userId).decrementAndGet();
            throw new WebSocketException("Maximum session limit reached for user: " + userId);
        }

        sessions.put(sessionId, session);
        sessionToUserId.put(sessionId, userId);
        log.info("Registered new session for user {} (sessionId: {})", userId, sessionId);
    }

    public void removeSession(String sessionId) {
        WebSocketSession session = sessions.remove(sessionId);
        if (session != null) {
            Long userId = sessionToUserId.remove(sessionId);
            if (userId != null) {
                userSessionCounts.computeIfPresent(userId, (k,v) -> {
                    v.decrementAndGet();
                    return v.get() == 0 ? null : v;
                });
                log.info("Removed session for user {} (sessionId: {})", userId, sessionId);
            }
        }
        try {
            session.close();
        } catch (IOException e) {
            log.warn("Error while closing session {}: {}", sessionId, e.getMessage());
        }
    }

    public boolean isAuthenticated(String sessionId) {
        return sessionToUserId.containsKey(sessionId);
    }

    public <T> void sendMessage(Long userId, T message) {
        sessionToUserId.entrySet().stream()
                .filter(entry -> userId.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .forEach( sessionId -> {
                    WebSocketSession session = sessions.get(sessionId);
                    if (session != null && session.isOpen()) {
                        try {
                            session.sendMessage(new WebSocketPayload<T>(message, mapper).toTextMessage());
                        } catch (IOException ex) {
                            log.error("Failed to send message to session {}: {}", sessionId, ex.getMessage());
                            removeSession(sessionId);
                    }
                }
        });
    }

    public int getSessionCountForUser(Long userId) throws WebSocketException {
        AtomicInteger sessionCount = userSessionCounts.get(userId);
        if (sessionCount == null) {
            throw new WebSocketException("There are no sessions for user " + userId);
        }

        return userSessionCounts.get(userId).get();
    }
}
