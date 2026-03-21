package com.ricky.message.config;

import com.ricky.message.enums.MessageBrokerType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessagingPropertiesTest {

    @Test
    void testDefaultValues() {
        MessagingProperties properties = new MessagingProperties();
        
        assertEquals(MessageBrokerType.KAFKA, properties.getBroker());
    }

    @Test
    void testKafkaProperties() {
        MessagingProperties properties = new MessagingProperties();
        
        MessagingProperties.KafkaProperties kafkaProperties = new MessagingProperties.KafkaProperties();
        kafkaProperties.setTopics(Arrays.asList("topic1", "topic2"));
        kafkaProperties.setConsumerGroup("test-group");
        kafkaProperties.setConcurrency(5);
        
        properties.setKafka(kafkaProperties);
        
        assertEquals(2, properties.getKafka().getTopics().size());
        assertEquals("test-group", properties.getKafka().getConsumerGroup());
        assertEquals(5, properties.getKafka().getConcurrency());
    }

    @Test
    void testRabbitmqProperties() {
        MessagingProperties properties = new MessagingProperties();
        
        MessagingProperties.RabbitmqProperties rabbitProperties = new MessagingProperties.RabbitmqProperties();
        rabbitProperties.setExchange("test.exchange");
        rabbitProperties.setQueuePrefix("test.queue.");
        rabbitProperties.setRoutingKeyPrefix("test.key.");
        
        properties.setRabbitmq(rabbitProperties);
        
        assertEquals("test.exchange", properties.getRabbitmq().getExchange());
        assertEquals("test.queue.", properties.getRabbitmq().getQueuePrefix());
        assertEquals("test.key.", properties.getRabbitmq().getRoutingKeyPrefix());
    }

    @Test
    void testSetBrokerType() {
        MessagingProperties properties = new MessagingProperties();
        properties.setBroker(MessageBrokerType.RABBITMQ);
        
        assertEquals(MessageBrokerType.RABBITMQ, properties.getBroker());
    }
}