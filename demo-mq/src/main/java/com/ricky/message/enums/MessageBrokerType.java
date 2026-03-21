package com.ricky.message.enums;

import lombok.Getter;

@Getter
public enum MessageBrokerType {

    KAFKA("kafka"),
    RABBITMQ("rabbitmq"),
    ;

    private final String value;

    MessageBrokerType(String value) {
        this.value = value;
    }

    public static MessageBrokerType fromValue(String value) {
        for (MessageBrokerType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message broker type: " + value);
    }
}