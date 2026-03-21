package com.ricky.message;

import java.util.Map;

public class Message<T> {

    private String topic;
    private String key;
    private T payload;
    private Map<String, String> headers;

    public Message() {}

    public Message(String topic, String key, T payload) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}