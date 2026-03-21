package com.ricky.message;

import java.util.function.Consumer;

public interface MessageListenerRegistry {

    <T> void register(String topic, Class<T> type, MessageConsumer<T> consumer);

    void registerConsumer(String topic, Consumer<Message<?>> consumer);

    ListenerWrapper<?> get(String topic);

}