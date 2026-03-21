package com.ricky.message.rabbit;

import com.ricky.message.SendResult;

import java.util.Map;

public class RabbitSendResult extends SendResult {

    private final String exchange;
    private final String routingKey;

    public RabbitSendResult(String topic, String exchange, String routingKey) {
        this.success = true;
        this.topic = topic;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public Object getMetadata() {
        return Map.of("exchange", exchange, "routingKey", routingKey);
    }
}