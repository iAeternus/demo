package com.ricky.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class MessagingTemplateImpl implements MessageTemplate {

    private final MessageProducer producer;

    @Override
    public <T> void send(String topic, T payload) {
        Message<T> msg = new Message<>(topic, null, payload);
        producer.send(msg);
    }

    @Override
    public <T> CompletableFuture<SendResult> sendAsync(String topic, T payload) {
        Message<T> msg = new Message<>(topic, null, payload);
        return producer.sendAsync(msg);
    }
}
