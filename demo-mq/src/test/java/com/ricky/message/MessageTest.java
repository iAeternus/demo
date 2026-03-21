package com.ricky.message;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void test_default_constructor() {
        Message<String> message = new Message<>();
        assertNull(message.getTopic());
        assertNull(message.getKey());
        assertNull(message.getPayload());
        assertNull(message.getHeaders());
    }

    @Test
    void test_parameterized_constructor() {
        String payload = "test payload";
        Message<String> message = new Message("test-topic", "test-key", payload);

        assertEquals("test-topic", message.getTopic());
        assertEquals("test-key", message.getKey());
        assertEquals(payload, message.getPayload());
    }

    @Test
    void test_setters_and_getters() {
        Message<OrderMessage> message = new Message<>();
        message.setTopic("order-topic");
        message.setKey("order-123");

        OrderMessage payload = new OrderMessage("order-123", 100.0);
        message.setPayload(payload);

        Map<String, String> headers = new HashMap<>();
        headers.put("traceId", "abc123");
        message.setHeaders(headers);

        assertEquals("order-topic", message.getTopic());
        assertEquals("order-123", message.getKey());
        assertEquals(payload, message.getPayload());
        assertEquals("abc123", message.getHeaders().get("traceId"));
    }

    @Test
    void test_topic_can_be_null() {
        Message<String> message = new Message<>(null, "key", "payload");
        assertNull(message.getTopic());
    }

    @Test
    void test_key_can_be_null() {
        Message<String> message = new Message<>("topic", null, "payload");
        assertNull(message.getKey());
    }

    @Test
    void test_payload_can_be_null() {
        Message<String> message = new Message<>("topic", "key", null);
        assertNull(message.getPayload());
    }

    @Test
    void test_different_payload_types() {
        Message<String> stringMessage = new Message<>("topic", "key", "string payload");
        Message<Integer> intMessage = new Message<>("topic", "key", 123);
        Message<Object> objectMessage = new Message<>("topic", "key", new Object());

        assertEquals("string payload", stringMessage.getPayload());
        assertEquals(123, intMessage.getPayload());
        assertNotNull(objectMessage.getPayload());
    }

    static class OrderMessage {
        private String orderId;
        private double amount;

        public OrderMessage() {
        }

        public OrderMessage(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}