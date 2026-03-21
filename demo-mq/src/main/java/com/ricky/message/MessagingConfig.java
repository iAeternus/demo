package com.ricky.message;

import com.ricky.message.config.MessagingProperties;
import com.ricky.message.kafka.KafkaListenerAdapter;
import com.ricky.message.kafka.KafkaMessageProducer;
import com.ricky.message.rabbit.RabbitListenerAdapter;
import com.ricky.message.rabbit.RabbitMessageProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MessagingProperties.class)
public class MessagingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "kafka", matchIfMissing = true)
    public MessageProducer kafkaMessageProducer(KafkaMessageProducer kafkaMessageProducer) {
        return kafkaMessageProducer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "rabbitmq")
    public MessageProducer rabbitMessageProducer(RabbitMessageProducer rabbitMessageProducer) {
        return rabbitMessageProducer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "kafka", matchIfMissing = true)
    public KafkaListenerAdapter kafkaListenerAdapter(KafkaListenerAdapter adapter) {
        return adapter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ricky.messaging", name = "broker", havingValue = "rabbitmq")
    public RabbitListenerAdapter rabbitListenerAdapter(RabbitListenerAdapter adapter) {
        return adapter;
    }
}