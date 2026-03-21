package com.ricky.message;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class DefaultMessageListenerRegistry implements MessageListenerRegistry {

    private final Map<String, ListenerWrapper<?>> consumers = new ConcurrentHashMap<>();

    @Override
    public <T> void register(String topic, Class<T> type, MessageConsumer<T> consumer) {
        consumers.put(topic, new ListenerWrapper<>(type, consumer));
    }

    @Override
    public void registerConsumer(String topic, Consumer<Message<?>> consumer) {
        consumers.put(topic, new ListenerWrapper<>(Object.class, msg -> {
            consumer.accept(msg);
        }));
    }

    @Override
    public ListenerWrapper<?> get(String topic) {
        return consumers.get(topic);
    }
}