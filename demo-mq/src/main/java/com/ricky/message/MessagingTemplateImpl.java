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

    @Override
    public <T> void sendInTransaction(String topic, T payload, Runnable transactionCallback) {
        Message<T> msg = new Message<>(topic, null, payload);
        
        // 在事务上下文中执行
        if (transactionCallback != null) {
            transactionCallback.run();
        }
        
        // 发送事务消息
        producer.sendInTransaction(msg);
    }
}