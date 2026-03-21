package com.ricky.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class Message<T> {

    private String topic;
    private String key;
    private T payload;
    private Map<String, String> headers;

    public Message() {
    }

    public Message(String topic, String key, T payload) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
    }

}