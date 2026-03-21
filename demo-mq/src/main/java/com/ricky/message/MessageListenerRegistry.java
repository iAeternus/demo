package com.ricky.message;

public interface MessageListenerRegistry {

    <T> void register(String topic, Class<T> type, MessageConsumer<T> consumer);

    ListenerWrapper<?> get(String topic);

}
