package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.WebSocketPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketSessionService {

    private final Map<WebSocketSession, Integer> userSessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session, Integer userId) {
        log.info("session is added  for user {} ", userId);
        userSessions.put(session, userId);
    }

    public void removeSession(WebSocketSession session) {
        Integer userId = userSessions.get(session);
        if (userId != null) {
            log.info("session is removed  for user {} ", userSessions.get(session));
            userSessions.remove(session);
        } else {
            log.warn("Attempted to remove nonexistent session {}", session.getId());
        }
    }

    public <TData> void sendMessage(Integer id, TData data) {
        var payload = new WebSocketPayload(data);
        for (Map.Entry<WebSocketSession, Integer> entry : userSessions.entrySet()) {
            if (entry.getValue().equals(id)) {
                WebSocketSession session = entry.getKey();
                if (session.isOpen()) {
                    try {
                        entry.getKey().sendMessage(payload.toTextMessage());
                    } catch (Exception e) {
                        log.error("Failed to send message to user: {}, {}", id, e.getMessage());
                    }
                } else {
                    log.warn("Session for user {} is closed, removing it.", id);
                    userSessions.remove(session);
                }
            }
        }
    }
}
