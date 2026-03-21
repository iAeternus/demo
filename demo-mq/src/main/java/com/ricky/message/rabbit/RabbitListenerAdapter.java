package com.ricky.message.rabbit;

import com.ricky.message.ListenerWrapper;
import com.ricky.message.Message;
import com.ricky.message.MessageListenerRegistry;
import com.ricky.message.config.MessagingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class RabbitListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitListenerAdapter.class);

    private final MessageListenerRegistry registry;
    private final MessagingProperties properties;

    public RabbitListenerAdapter(MessageListenerRegistry registry, MessagingProperties properties) {
        this.registry = registry;
        this.properties = properties;
    }

    @RabbitListener(queues = "#{@rickyRabbitQueueList}")
    public void onMessage(org.springframework.amqp.core.Message amqpMessage) {
        String routingKey = amqpMessage.getMessageProperties().getReceivedRoutingKey();
        String topic = extractTopic(routingKey);

        try {
            ListenerWrapper<?> wrapper = registry.get(topic);

            if (wrapper != null) {
                Message<Object> msg = new Message<>();
                msg.setTopic(topic);
                msg.setPayload(amqpMessage.getBody());

                wrapper.handle(msg);
                log.debug("Message consumed from RabbitMQ: topic={}", topic);
            } else {
                log.warn("No consumer registered for topic: {}", topic);
            }
        } catch (Exception ex) {
            log.error("Error processing message from RabbitMQ: topic={}", topic, ex);
            throw new RuntimeException(ex);
        }
    }

    private String extractTopic(String routingKey) {
        String prefix = properties.getRabbitmq().getRoutingKeyPrefix();
        if (routingKey.startsWith(prefix)) {
            return routingKey.substring(prefix.length());
        }
        return routingKey;
    }

    @Component
    public static class RabbitQueueListProvider {
        private final MessagingProperties properties;

        public RabbitQueueListProvider(MessagingProperties properties) {
            this.properties = properties;
        }

        public List<String> rickyRabbitQueueList() {
            return properties.getKafka().getTopics().stream()
                    .map(topic -> properties.getRabbitmq().getQueuePrefix() + topic)
                    .collect(Collectors.toList());
        }
    }
}