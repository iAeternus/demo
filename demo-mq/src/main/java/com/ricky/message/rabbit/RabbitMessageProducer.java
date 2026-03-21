package com.ricky.message.rabbit;

import com.ricky.message.FailedSendResult;
import com.ricky.message.Message;
import com.ricky.message.MessageProducer;
import com.ricky.message.SendResult;
import com.ricky.message.config.MessagingProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class RabbitMessageProducer implements MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties properties;

    public RabbitMessageProducer(RabbitTemplate rabbitTemplate, MessagingProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @Override
    public <T> SendResult send(Message<T> message) {
        try {
            String exchange = properties.getRabbitmq().getExchange();
            String routingKey = properties.getRabbitmq().getRoutingKeyPrefix() + message.getTopic();

            rabbitTemplate.convertAndSend(exchange, routingKey, message.getPayload());

            return new RabbitSendResult(message.getTopic(), exchange, routingKey);
        } catch (Exception ex) {
            log.error("Failed to send message to RabbitMQ: topic={}", message.getTopic(), ex);
            return new FailedSendResult(message.getTopic(), ex);
        }
    }

    @Override
    public <T> CompletableFuture<SendResult> sendAsync(Message<T> message) {
        return CompletableFuture.supplyAsync(() -> send(message));
    }

    @Override
    public <T> SendResult sendInTransaction(Message<T> message) {
        return rabbitTemplate.execute(status -> {
            send(message);
            return new RabbitSendResult(
                    message.getTopic(),
                    properties.getRabbitmq().getExchange(),
                    properties.getRabbitmq().getRoutingKeyPrefix() + message.getTopic()
            );
        });
    }
}