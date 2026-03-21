package com.ricky.message.order.publisher;

import com.ricky.message.Message;
import com.ricky.message.MessageProducer;
import com.ricky.message.SendResult;
import com.ricky.message.order.entity.OrderCreatedEvent;
import com.ricky.message.order.entity.OrderPaidEvent;
import com.ricky.message.order.entity.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    public static final String TOPIC_ORDER_CREATED = "order-created";
    public static final String TOPIC_ORDER_PAID = "order-paid";
    public static final String TOPIC_ORDER_SHIPPED = "order-shipped";

    private final MessageProducer messageProducer;

    public OrderEventPublisher(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public SendResult publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent: orderId={}, userId={}", event.getOrderId(), event.getUserId());
        
        Message<OrderCreatedEvent> message = new Message<>(
                TOPIC_ORDER_CREATED,
                event.getOrderId(),
                event
        );
        
        SendResult result = messageProducer.send(message);
        
        if (result.isSuccess()) {
            log.info("OrderCreatedEvent published successfully: topic={}, partition={}, offset={}", 
                    result.getTopic(), 
                    result.getMetadata() != null ? ((java.util.Map<?, ?>) result.getMetadata()).get("partition") : "N/A",
                    result.getMetadata() != null ? ((java.util.Map<?, ?>) result.getMetadata()).get("offset") : "N/A");
        } else {
            log.error("OrderCreatedEvent publish failed: orderId={}", event.getOrderId(), result.getError());
        }
        
        return result;
    }

    public SendResult publishOrderPaid(OrderPaidEvent event) {
        log.info("Publishing OrderPaidEvent: orderId={}, paymentId={}", event.getOrderId(), event.getPaymentId());
        
        Message<OrderPaidEvent> message = new Message<>(
                TOPIC_ORDER_PAID,
                event.getOrderId(),
                event
        );
        
        SendResult result = messageProducer.send(message);
        
        if (result.isSuccess()) {
            log.info("OrderPaidEvent published successfully: topic={}", result.getTopic());
        } else {
            log.error("OrderPaidEvent publish failed: orderId={}", event.getOrderId(), result.getError());
        }
        
        return result;
    }

    public SendResult publishOrderShipped(OrderShippedEvent event) {
        log.info("Publishing OrderShippedEvent: orderId={}, carrier={}", event.getOrderId(), event.getCarrier());
        
        Message<OrderShippedEvent> message = new Message<>(
                TOPIC_ORDER_SHIPPED,
                event.getOrderId(),
                event
        );
        
        SendResult result = messageProducer.send(message);
        
        if (result.isSuccess()) {
            log.info("OrderShippedEvent published successfully: topic={}", result.getTopic());
        } else {
            log.error("OrderShippedEvent publish failed: orderId={}", event.getOrderId(), result.getError());
        }
        
        return result;
    }

    public CompletableFuture<SendResult> publishOrderCreatedAsync(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent async: orderId={}", event.getOrderId());
        
        Message<OrderCreatedEvent> message = new Message<>(
                TOPIC_ORDER_CREATED,
                event.getOrderId(),
                event
        );
        
        return messageProducer.sendAsync(message);
    }
}