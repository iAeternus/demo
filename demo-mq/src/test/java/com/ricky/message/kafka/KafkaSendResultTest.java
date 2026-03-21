package com.ricky.message.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaSendResultTest {

    @Test
    void testSuccessfulSendResult() {
        KafkaSendResult result = new KafkaSendResult("test-topic", 0, 100L);
        
        assertTrue(result.isSuccess());
        assertEquals("test-topic", result.getTopic());
        assertEquals(0, result.getPartition());
        assertEquals(100L, result.getOffset());
    }

    @Test
    void testKafkaSendResultMetadata() {
        KafkaSendResult result = new KafkaSendResult("test-topic", 1, 200L);
        
        Object metadata = result.getMetadata();
        assertNotNull(metadata);
        assertTrue(metadata instanceof java.util.Map);
        
        java.util.Map<?, ?> metaMap = (java.util.Map<?, ?>) metadata;
        assertEquals(1, metaMap.get("partition"));
        assertEquals(200L, metaMap.get("offset"));
    }

    @Test
    void testKafkaSendResultShouldNotRetry() {
        KafkaSendResult result = new KafkaSendResult("test-topic", 0, 100L);
        
        assertFalse(result.shouldRetry());
    }

    @Test
    void testDifferentPartitionAndOffset() {
        KafkaSendResult result = new KafkaSendResult("order-topic", 3, 5000L);
        
        assertEquals(3, result.getPartition());
        assertEquals(5000L, result.getOffset());
    }
}