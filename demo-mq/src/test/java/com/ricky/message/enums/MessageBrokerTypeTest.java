package com.ricky.message.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBrokerTypeTest {

    @Test
    void testKafkaValue() {
        assertEquals("kafka", MessageBrokerType.KAFKA.getValue());
    }

    @Test
    void testRabbitmqValue() {
        assertEquals("rabbitmq", MessageBrokerType.RABBITMQ.getValue());
    }

    @Test
    void testFromValueKafka() {
        MessageBrokerType type = MessageBrokerType.fromValue("kafka");
        assertEquals(MessageBrokerType.KAFKA, type);
    }

    @Test
    void testFromValueRabbitmq() {
        MessageBrokerType type = MessageBrokerType.fromValue("rabbitmq");
        assertEquals(MessageBrokerType.RABBITMQ, type);
    }

    @Test
    void testFromValueCaseInsensitive() {
        assertEquals(MessageBrokerType.KAFKA, MessageBrokerType.fromValue("KAFKA"));
        assertEquals(MessageBrokerType.KAFKA, MessageBrokerType.fromValue("KaFkA"));
        assertEquals(MessageBrokerType.RABBITMQ, MessageBrokerType.fromValue("RABBITMQ"));
    }

    @Test
    void testFromValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> MessageBrokerType.fromValue("invalid"));
    }
}