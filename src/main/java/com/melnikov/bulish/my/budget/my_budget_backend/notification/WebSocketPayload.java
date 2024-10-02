package com.melnikov.bulish.my.budget.my_budget_backend.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

@Slf4j
@Data
public class WebSocketPayload<TData extends Object> {

    private static final ObjectMapper mapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();

    private TData data;

    public WebSocketPayload(TData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        try {
            return mapper.writer().writeValueAsString(this.data);
        } catch (JsonProcessingException e) {
            log.error(e.toString());
            e.printStackTrace();

            return "Invalid WebSocketPayload";
        }
    }

    public TextMessage toTextMessage() {
        return new TextMessage(toString());
    }
}