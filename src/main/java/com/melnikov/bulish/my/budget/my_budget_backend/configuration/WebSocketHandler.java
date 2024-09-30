package com.melnikov.bulish.my.budget.my_budget_backend.configuration;

import com.melnikov.bulish.my.budget.my_budget_backend.notification.WebSocketSessionService;
import com.melnikov.bulish.my.budget.my_budget_backend.token.JwtTokenService;
import com.melnikov.bulish.my.budget.my_budget_backend.user.User;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionService sessionService;
    private final UserServiceImpl userService;
    private final JwtTokenService jwtTokenService;
    private TimerTask task = null;

    public WebSocketHandler(WebSocketSessionService sessionService, UserServiceImpl userService, JwtTokenService jwtTokenService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
       String username = jwtTokenService.extractUsername(message.getPayload());
        User user = null;

        if (username == null) {
            sessionService.removeSession(session);
            if (session.isOpen()) {
                session.close();
            }
            return;
        }
        user = userService.findByUserName(username);

        if (user == null) {
            sessionService.removeSession(session);
            if (session.isOpen()) {
                session.close();
            }
           return;
        }
        sessionService.addSession(session, user.getId());

        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Timer timer = new Timer();
         task = new TimerTask() {
             public void run() {
                   try {
                       session.close();
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
                }
            };

        timer.schedule(task, 30000);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed: {} status = {}", session, status);
        sessionService.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        log.error("Transport error: {}", throwable.getMessage());
        sessionService.removeSession(webSocketSession);
    }

}