package com.ricky.message;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class EventListenerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final MessageListenerRegistry registry;
    private final EventListenerScanner scanner;

    public EventListenerBootstrap(MessageListenerRegistry registry, EventListenerScanner scanner) {
        this.registry = registry;
        this.scanner = scanner;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        scanner.scanAndRegister(registry);
    }
}