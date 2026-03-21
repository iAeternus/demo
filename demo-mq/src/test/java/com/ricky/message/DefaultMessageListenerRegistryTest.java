package com.ricky.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMessageListenerRegistryTest {

    private DefaultMessageListenerRegistry registry;

    @BeforeEach
    void set_up() {
        registry = new DefaultMessageListenerRegistry();
    }

    @Test
    void test_register_consumer() {
        MessageConsumer<String> consumer = msg -> System.out.println("Received: " + msg.getPayload());
        
        registry.register("test-topic", String.class, consumer);
        
        ListenerWrapper<?> wrapper = registry.get("test-topic");
        assertNotNull(wrapper);
    }

    @Test
    void test_get_non_existent_topic() {
        ListenerWrapper<?> wrapper = registry.get("non-existent-topic");
        assertNull(wrapper);
    }

    @Test
    void test_register_multiple_consumers() {
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
    void test_overwrite_existing_consumer() {
        MessageConsumer<String> consumer1 = msg -> {};
        MessageConsumer<String> consumer2 = msg -> {};

        registry.register("test-topic", String.class, consumer1);
        registry.register("test-topic", String.class, consumer2);

        ListenerWrapper<?> wrapper = registry.get("test-topic");
        assertNotNull(wrapper);
    }
}