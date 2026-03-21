package com.ricky.message.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageBrokerTypeTest {

    @Test
    void test_kafka_value() {
        assertEquals("kafka", MessageBrokerType.KAFKA.getValue());
    }

    @Test
    void test_rabbitmq_value() {
        assertEquals("rabbitmq", MessageBrokerType.RABBITMQ.getValue());
    }

    @Test
    void test_from_value_kafka() {
        MessageBrokerType type = MessageBrokerType.fromValue("kafka");
        assertEquals(MessageBrokerType.KAFKA, type);
    }

    @Test
    void test_from_value_rabbitmq() {
        MessageBrokerType type = MessageBrokerType.fromValue("rabbitmq");
        assertEquals(MessageBrokerType.RABBITMQ, type);
    }

    @Test
    void test_from_value_case_insensitive() {
        assertEquals(MessageBrokerType.KAFKA, MessageBrokerType.fromValue("KAFKA"));
        assertEquals(MessageBrokerType.KAFKA, MessageBrokerType.fromValue("KaFkA"));
        assertEquals(MessageBrokerType.RABBITMQ, MessageBrokerType.fromValue("RABBITMQ"));
    }

    @Test
    void test_from_value_invalid() {
        assertThrows(IllegalArgumentException.class, () -> MessageBrokerType.fromValue("invalid"));
    }
}