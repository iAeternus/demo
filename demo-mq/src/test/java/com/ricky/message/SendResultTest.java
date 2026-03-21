package com.ricky.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendResultTest {

    @Test
    void testFailedSendResultShouldRetry() {
        FailedSendResult result = new FailedSendResult("test-topic", new RuntimeException("test error"));
        
        assertFalse(result.isSuccess());
        assertEquals("test-topic", result.getTopic());
        assertNotNull(result.getError());
        assertTrue(result.shouldRetry());
    }

    @Test
    void testFailedSendResultMetadata() {
        FailedSendResult result = new FailedSendResult("test-topic", new RuntimeException("test error"));
        
        assertNull(result.getMetadata());
    }

    @Test
    void testSendResultShouldRetryForFailedResult() {
        SendResult result = new FailedSendResult("test-topic", new RuntimeException("error"));
        assertTrue(result.shouldRetry());
    }
}