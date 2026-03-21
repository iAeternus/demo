package com.ricky.message;

public class ListenerWrapper<T> {

    private final Class<T> type;
    private final MessageConsumer<T> consumer;

    public ListenerWrapper(Class<T> type, MessageConsumer<T> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    public void handle(Message<?> msg) throws Exception {
        Object payload = msg.getPayload();

        if (!type.isInstance(payload)) {
            throw new IllegalArgumentException(
                    "Payload type mismatch, expected: " + type + ", actual: " + payload.getClass()
            );
        }

        Message<T> casted = new Message<>();
        casted.setTopic(msg.getTopic());
        casted.setKey(msg.getKey());
        casted.setPayload(type.cast(payload));

        consumer.onMessage(casted);
    }
}