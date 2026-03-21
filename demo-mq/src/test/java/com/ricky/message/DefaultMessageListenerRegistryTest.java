package com.ricky.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMessageListenerRegistryTest {

    private DefaultMessageListenerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new DefaultMessageListenerRegistry();
    }

    @Test
    void testRegisterConsumer() {
        MessageConsumer<String> consumer = msg -> System.out.println("Received: " + msg.getPayload());
        
        registry.register("test-topic", String.class, consumer);
        
        ListenerWrapper<?> wrapper = registry.get("test-topic");
        assertNotNull(wrapper);
    }

    @Test
    void testGetNonExistentTopic() {
        ListenerWrapper<?> wrapper = registry.get("non-existent-topic");
        assertNull(wrapper);
    }

    @Test
    void testRegisterMultipleConsumers() {
        MessageConsumer<String> consumer1 = msg -> {};
        MessageConsumer<Integer> consumer2 = msg -> {};

        registry.register("topic1", String.class, consumer1);
        registry.register("topic2", Integer.class, consumer2);

        ListenerWrapper<?> wrapper1 = registry.get("topic1");
        ListenerWrapper<?> wrapper2 = registry.get("topic2");

        assertNotNull(wrapper1);
        assertNotNull(wrapper2);
    }

    @Test
    void testOverwriteExistingConsumer() {
        MessageConsumer<String> consumer1 = msg -> {};
        MessageConsumer<String> consumer2 = msg -> {};

        registry.register("test-topic", String.class, consumer1);
        registry.register("test-topic", String.class, consumer2);

        ListenerWrapper<?> wrapper = registry.get("test-topic");
        assertNotNull(wrapper);
    }
}