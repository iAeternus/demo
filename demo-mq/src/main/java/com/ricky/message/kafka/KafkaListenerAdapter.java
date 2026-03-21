package com.ricky.message.kafka;

import com.ricky.message.ListenerWrapper;
import com.ricky.message.Message;
import com.ricky.message.MessageListenerRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListenerAdapter {

    private final MessageListenerRegistry registry;

    @KafkaListener(topics = "#{rickyMessagingProperties.kafka.topics}", groupId = "#{rickyMessagingProperties.kafka.consumerGroup}")
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) throws Exception {
        Message<Object> msg = new Message<>();
        msg.setTopic(record.topic());
        msg.setKey(record.key());
        msg.setPayload(record.value());

        ListenerWrapper<?> wrapper = registry.get(record.topic());

        if (wrapper != null) {
            wrapper.handle(msg);
        }

        acknowledgment.acknowledge();
    }
}