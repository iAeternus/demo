package com.ricky.message.order.consumer;

import com.ricky.message.Message;
import com.ricky.message.MessageConsumer;
import com.ricky.message.order.entity.OrderCreatedEvent;
import com.ricky.message.order.entity.OrderPaidEvent;
import com.ricky.message.order.entity.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final List<OrderCreatedEvent> createdEvents = new CopyOnWriteArrayList<>();
    private final List<OrderPaidEvent> paidEvents = new CopyOnWriteArrayList<>();
    private final List<OrderShippedEvent> shippedEvents = new CopyOnWriteArrayList<>();

    public MessageConsumer<OrderCreatedEvent> orderCreatedConsumer = message -> {
        OrderCreatedEvent event = message.getPayload();
        log.info("Received OrderCreatedEvent: {}", event);
        
        createdEvents.add(event);
        
        log.info("Order created successfully: orderId={}, amount={}, status={}", 
                event.getOrderId(), event.getTotalAmount(), event.getOrderStatus());
    };

    public MessageConsumer<OrderPaidEvent> orderPaidConsumer = message -> {
        OrderPaidEvent event = message.getPayload();
        log.info("Received OrderPaidEvent: {}", event);
        
        paidEvents.add(event);
        
        log.info("Order paid successfully: orderId={}, paymentId={}, amount={}", 
                event.getOrderId(), event.getPaymentId(), event.getPaidAmount());
    };

    public MessageConsumer<OrderShippedEvent> orderShippedConsumer = message -> {
        OrderShippedEvent event = message.getPayload();
        log.info("Received OrderShippedEvent: {}", event);
        
        shippedEvents.add(event);
        
        log.info("Order shipped successfully: orderId={}, carrier={}, trackingNumber={}", 
                event.getOrderId(), event.getCarrier(), event.getTrackingNumber());
    };

    public List<OrderCreatedEvent> getCreatedEvents() {
        return new ArrayList<>(createdEvents);
    }

    public List<OrderPaidEvent> getPaidEvents() {
        return new ArrayList<>(paidEvents);
    }

    public List<OrderShippedEvent> getShippedEvents() {
        return new ArrayList<>(shippedEvents);
    }

    public void clear() {
        createdEvents.clear();
        paidEvents.clear();
        shippedEvents.clear();
    }

    public boolean hasReceivedCreatedEvent(String orderId) {
        return createdEvents.stream().anyMatch(e -> e.getOrderId().equals(orderId));
    }

    public boolean hasReceivedPaidEvent(String orderId) {
        return paidEvents.stream().anyMatch(e -> e.getOrderId().equals(orderId));
    }

    public boolean hasReceivedShippedEvent(String orderId) {
        return shippedEvents.stream().anyMatch(e -> e.getOrderId().equals(orderId));
    }
}