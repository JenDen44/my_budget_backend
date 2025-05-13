package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;

import java.io.Serializable;

@Slf4j
@Getter
public class WebSocketPayload<TData extends Object> implements Serializable {

    @Autowired
    private ObjectMapper mapper;

    private TData data;

    public WebSocketPayload(TData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        try {
            return mapper.writer().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize WebSocketPayload: {}", e.getMessage());
            return "Invalid WebSocketPayload";
        }
    }

    public TextMessage toTextMessage() {
        return new TextMessage(toString());
    }
}