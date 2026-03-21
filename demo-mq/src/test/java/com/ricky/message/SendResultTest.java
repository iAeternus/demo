package com.ricky.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendResultTest {

    @Test
    void test_failed_send_result_should_retry() {
        FailedSendResult result = new FailedSendResult("test-topic", new RuntimeException("test error"));
        
        assertFalse(result.isSuccess());
        assertEquals("test-topic", result.getTopic());
        assertNotNull(result.getError());
        assertTrue(result.shouldRetry());
    }

    @Test
    void test_failed_send_result_metadata() {
        FailedSendResult result = new FailedSendResult("test-topic", new RuntimeException("test error"));
        
        assertNull(result.getMetadata());
    }

    @Test
    void test_send_result_should_retry_for_failed_result() {
        SendResult result = new FailedSendResult("test-topic", new RuntimeException("error"));
        assertTrue(result.shouldRetry());
    }
}