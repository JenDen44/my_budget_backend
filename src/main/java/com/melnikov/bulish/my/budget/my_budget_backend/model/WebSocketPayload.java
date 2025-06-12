package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import java.io.Serializable;

@Slf4j
@Getter
public class WebSocketPayload<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;

    private final transient ObjectMapper mapper;

    public WebSocketPayload(T data, ObjectMapper mapper) {
        this.data = data;
        this.mapper = mapper;
    }

    public TextMessage toTextMessage() {
        try {
            return new TextMessage(toString());
        } catch (Exception ex) {
            log.error("Failed to create TextMessage: {}", ex.getMessage());
            throw new RuntimeException("Failed to serialize WebSocketPayload", ex);
        }
    }

    @Override
    public String toString() {
        try {
            return mapper.writer().writeValueAsString(this.data);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize WebSocketPayload: {}", e.getMessage());
            return "Invalid WebSocketPayload";
        }
    }
}