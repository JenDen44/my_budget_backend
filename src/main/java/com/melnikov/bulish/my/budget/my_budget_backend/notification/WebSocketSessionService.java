package com.melnikov.bulish.my.budget.my_budget_backend.notification;

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
        userSessions.put(session, userId);
    }

    public void removeSession(WebSocketSession session) {
        userSessions.remove(session);
    }

    public <TData> void sendMessage(Integer id, TData data) {
        var payload = new WebSocketPayload(data);

        for (Map.Entry<WebSocketSession, Integer> entry : userSessions.entrySet()) {
            if (entry.getValue().equals(id)) {
                try {
                    entry.getKey().sendMessage(payload.toTextMessage());
                } catch (Exception e) {
                    log.error("Failed to send message to user: {}", id, e);
                }
            }
        }
    }
}
