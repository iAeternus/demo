package com.ricky.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingTemplateImplTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void test_send_message() throws Exception {
        SendResult<String, Object> mockResult = mock(SendResult.class);
        var mockRecordMetadata = mock(org.apache.kafka.clients.producer.RecordMetadata.class);
        
        when(mockRecordMetadata.topic()).thenReturn("test-topic");
        when(mockRecordMetadata.partition()).thenReturn(0);
        when(mockRecordMetadata.offset()).thenReturn(1L);
        
        when(mockResult.getRecordMetadata()).thenReturn(mockRecordMetadata);

        when(kafkaTemplate.send(eq("test-topic"), isNull(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResult));

        com.ricky.message.kafka.KafkaMessageProducer producer = new com.ricky.message.kafka.KafkaMessageProducer(kafkaTemplate);
        
        com.ricky.message.Message<String> message = new com.ricky.message.Message<>("test-topic", null, "test payload");
        com.ricky.message.SendResult result = producer.send(message);

        assertTrue(result.isSuccess());
        assertEquals("test-topic", result.getTopic());
    }

    @Test
    void test_send_message_failure() {
        when(kafkaTemplate.send(anyString(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Send failed")));

        com.ricky.message.kafka.KafkaMessageProducer producer = new com.ricky.message.kafka.KafkaMessageProducer(kafkaTemplate);
        
        com.ricky.message.Message<String> message = new com.ricky.message.Message<>("test-topic", null, "test payload");
        com.ricky.message.SendResult result = producer.send(message);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void test_send_async_message() {
        SendResult<String, Object> mockResult = mock(SendResult.class);
        var mockRecordMetadata = mock(org.apache.kafka.clients.producer.RecordMetadata.class);
        
        when(mockRecordMetadata.topic()).thenReturn("test-topic");
        when(mockRecordMetadata.partition()).thenReturn(0);
        when(mockRecordMetadata.offset()).thenReturn(1L);
        
        when(mockResult.getRecordMetadata()).thenReturn(mockRecordMetadata);
        
        when(kafkaTemplate.send(anyString(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mockResult));

        com.ricky.message.kafka.KafkaMessageProducer producer = new com.ricky.message.kafka.KafkaMessageProducer(kafkaTemplate);
        
        com.ricky.message.Message<String> message = new com.ricky.message.Message<>("test-topic", null, "test payload");
        
        CompletableFuture<com.ricky.message.SendResult> asyncResult = producer.sendAsync(message);
        
        assertNotNull(asyncResult);
        com.ricky.message.SendResult result = asyncResult.join();
        assertTrue(result.isSuccess());
    }
}