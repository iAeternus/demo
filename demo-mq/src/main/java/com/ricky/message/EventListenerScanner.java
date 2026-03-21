package com.ricky.message;

import com.ricky.message.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class EventListenerScanner {

    private static final Logger log = LoggerFactory.getLogger(EventListenerScanner.class);

    private final ClassPathScanningCandidateComponentProvider scanner;
    private final ConcurrentHashMap<String, Object> registeredListeners = new ConcurrentHashMap<>();

    public EventListenerScanner() {
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EventListener.class));
    }

    public void scanAndRegister(MessageListenerRegistry registry) {
        try {
            Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.ricky");

            for (BeanDefinition bd : candidates) {
                String className = bd.getBeanClassName();
                if (className == null) continue;

                Class<?> clazz = Class.forName(className);
                EventListener annotation = clazz.getAnnotation(EventListener.class);

                if (annotation != null) {
                    registerListener(clazz, annotation, registry);
                }
            }

            log.info("Registered {} event listeners", registeredListeners.size());

        } catch (Exception e) {
            log.error("Failed to scan event listeners", e);
        }
    }

    private void registerListener(Class<?> clazz, EventListener annotation,
                                  MessageListenerRegistry registry) {
        try {
            String topic = annotation.topic();

            // 创建消费者实例
            Object consumerBean = clazz.getDeclaredConstructor().newInstance();

            // 使用通用的消费者包装
            Consumer<Message<?>> consumer = message -> {
                try {
                    invokeOnMessage(consumerBean, message.getPayload());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            // 注册消费者
            registry.registerConsumer(topic, consumer);
            registeredListeners.put(topic, consumerBean);

            log.info("Registered listener: topic={}, bean={}", topic, clazz.getSimpleName());

        } catch (Exception e) {
            log.error("Failed to register listener: {}", clazz.getName(), e);
        }
    }

    private void invokeOnMessage(Object bean, Object payload) throws Exception {
        for (java.lang.reflect.Method method : bean.getClass().getDeclaredMethods()) {
            if ("onMessage".equals(method.getName())) {
                method.setAccessible(true);

                // 尝试查找 Message 类型的参数
                Class<?>[] params = method.getParameterTypes();
                if (params.length > 0) {
                    if (params[0].isAssignableFrom(payload.getClass())) {
                        method.invoke(bean, payload);
                        return;
                    }
                }

                // 如果没有参数匹配，尝试直接调用（payload 作为参数）
                method.invoke(bean, payload);
                return;
            }
        }
        throw new IllegalStateException("No onMessage method found in: " + bean.getClass().getName());
    }
}