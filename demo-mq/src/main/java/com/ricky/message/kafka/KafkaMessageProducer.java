package com.ricky.message.kafka;

import com.ricky.message.FailedSendResult;
import com.ricky.message.Message;
import com.ricky.message.MessageProducer;
import com.ricky.message.SendResult;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Primary
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public <T> SendResult send(Message<T> message) {
        try {
            var result = kafkaTemplate.send(
                    message.getTopic(),
                    message.getKey(),
                    message.getPayload()
            ).get();

            RecordMetadata meta = result.getRecordMetadata();
            return new KafkaSendResult(meta.topic(), meta.partition(), meta.offset());
        } catch (Exception ex) {
            return new FailedSendResult(message.getTopic(), ex);
        }
    }

    @Override
    public <T> CompletableFuture<SendResult> sendAsync(Message<T> message) {
        return kafkaTemplate.send(
                message.getTopic(),
                message.getKey(),
                message.getPayload()
        ).handle((result, ex) -> {
            if (ex != null) {
                return new FailedSendResult(message.getTopic(), ex);
            }

            var meta = result.getRecordMetadata();
            return new KafkaSendResult(
                    meta.topic(),
                    meta.partition(),
                    meta.offset()
            );
        });
    }

    @Override
    public <T> SendResult sendInTransaction(Message<T> message) {
        return kafkaTemplate.executeInTransaction(kt -> {
            kt.send(message.getTopic(), message.getKey(), message.getPayload());
            return new KafkaSendResult(message.getTopic(), 0, -1L);
        });
    }
}
