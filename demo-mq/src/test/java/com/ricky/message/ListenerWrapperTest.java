package com.ricky.message;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListenerWrapperTest {

    @Test
    void test_handle_message_with_matching_type() throws Exception {
        TestConsumer consumer = new TestConsumer();
        ListenerWrapper<TestMessage> wrapper = new ListenerWrapper<>(TestMessage.class, consumer);

        Message<TestMessage> msg = new Message<>();
        msg.setTopic("test-topic");
        msg.setKey("test-key");
        
        TestMessage payload = new TestMessage();
        payload.id = "123";
        payload.name = "test";
        msg.setPayload(payload);

        wrapper.handle(msg);

        assertTrue(consumer.isCalled());
        assertNotNull(consumer.getReceivedMessage());
        assertEquals("test-topic", consumer.getReceivedMessage().getTopic());
        assertEquals("123", consumer.getReceivedMessage().getPayload().id);
    }

    @Test
    void test_handle_message_with_mismatched_type() {
        ListenerWrapper<String> wrapper = new ListenerWrapper<>(String.class, msg -> {});

        Message<Integer> msg = new Message<>();
        msg.setPayload(123);

        assertThrows(IllegalArgumentException.class, () -> wrapper.handle(msg));
    }

    @Test
    void test_message_payload_preserved_in_wrapper() throws Exception {
        TestConsumer consumer = new TestConsumer();
        ListenerWrapper<TestMessage> wrapper = new ListenerWrapper<>(TestMessage.class, consumer);

        TestMessage testPayload = new TestMessage();
        testPayload.id = "test-id";
        testPayload.name = "test-name";

        Message<TestMessage> msg = new Message<>();
        msg.setTopic("test-topic");
        msg.setPayload(testPayload);

        wrapper.handle(msg);

        assertNotNull(consumer.getReceivedMessage());
        assertEquals("test-id", consumer.getReceivedMessage().getPayload().id);
        assertEquals("test-name", consumer.getReceivedMessage().getPayload().name);
    }

    @Test
    void test_message_key_preserved() throws Exception {
        TestConsumer consumer = new TestConsumer();
        ListenerWrapper<TestMessage> wrapper = new ListenerWrapper<>(TestMessage.class, consumer);

        TestMessage payload = new TestMessage();
        payload.id = "123";

        Message<TestMessage> msg = new Message<>();
        msg.setTopic("test-topic");
        msg.setKey("test-key");
        msg.setPayload(payload);

        wrapper.handle(msg);

        assertEquals("test-key", consumer.getReceivedMessage().getKey());
    }

    @Getter
    static class TestConsumer implements MessageConsumer<TestMessage> {
        private boolean called = false;
        private Message<TestMessage> receivedMessage;

        @Override
        public void onMessage(Message<TestMessage> message) {
            this.called = true;
            this.receivedMessage = message;
        }
    }

    static class TestMessage {
        public String id;
        public String name;
    }
}