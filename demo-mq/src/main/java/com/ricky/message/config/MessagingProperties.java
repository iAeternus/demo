package com.ricky.message.config;

import com.ricky.message.enums.MessageBrokerType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component("rickyMessagingProperties")
@ConfigurationProperties(prefix = "ricky.messaging")
public class MessagingProperties {

    private MessageBrokerType broker = MessageBrokerType.KAFKA;
    private KafkaProperties kafka = new KafkaProperties();
    private RabbitmqProperties rabbitmq = new RabbitmqProperties();

    @Setter
    @Getter
    public static class KafkaProperties {
        private List<String> topics;
        private String consumerGroup = "ricky-consumer-group";
        private int concurrency = 3;
    }

    @Setter
    @Getter
    public static class RabbitmqProperties {
        private String exchange = "ricky.exchange";
        private String queuePrefix = "ricky.queue.";
        private String routingKeyPrefix = "ricky.key.";
    }
}