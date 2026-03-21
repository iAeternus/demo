package com.ricky.message.order;

import com.ricky.message.MessageListenerRegistry;
import com.ricky.message.SendResult;
import com.ricky.message.order.consumer.OrderEventConsumer;
import com.ricky.message.order.entity.OrderCreatedEvent;
import com.ricky.message.order.entity.OrderPaidEvent;
import com.ricky.message.order.entity.OrderShippedEvent;
import com.ricky.message.order.publisher.OrderEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.enable-auto-commit=true",
        "ricky.messaging.broker=kafka",
        "ricky.messaging.kafka.topics=order-created,order-paid,order-shipped"
})
public class OrderEventIntegrationTest {

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Autowired
    private OrderEventConsumer orderEventConsumer;

    @Autowired
    private MessageListenerRegistry registry;

    @BeforeEach
    void setUp() {
        orderEventConsumer.clear();

        registry.register("order-created", OrderCreatedEvent.class, orderEventConsumer.orderCreatedConsumer);
        registry.register("order-paid", OrderPaidEvent.class, orderEventConsumer.orderPaidConsumer);
        registry.register("order-shipped", OrderShippedEvent.class, orderEventConsumer.orderShippedConsumer);
    }

    @Test
    void testPublishAndConsumeOrderCreatedEvent() throws Exception {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId("USER-123")
                .productId("PROD-456")
                .productName("iPhone 15 Pro Max")
                .quantity(1)
                .unitPrice(new BigDecimal("9999.00"))
                .totalAmount(new BigDecimal("9999.00"))
                .orderStatus("CREATED")
                .shippingAddress("Beijing, Chaoyang District")
                .createdAt(LocalDateTime.now())
                .build();

        var result = orderEventPublisher.publishOrderCreated(event);

        if (!result.isSuccess()) {
            log.error("Failed to publish", result.getError());
        }

        assertTrue(result.isSuccess(), "Publish should succeed: " + result.getError());

        Thread.sleep(2000);

        assertTrue(orderEventConsumer.hasReceivedCreatedEvent(orderId),
                "Consumer should receive OrderCreatedEvent");

        var receivedEvents = orderEventConsumer.getCreatedEvents();
        var received = receivedEvents.stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .findFirst();

        assertTrue(received.isPresent());
        assertEquals("USER-123", received.get().getUserId());
        assertEquals("iPhone 15 Pro Max", received.get().getProductName());
    }

    @Test
    void testPublishAndConsumeOrderPaidEvent() throws Exception {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        OrderPaidEvent event = OrderPaidEvent.builder()
                .orderId(orderId)
                .paymentId("PAY-" + UUID.randomUUID().toString().substring(0, 8))
                .paymentMethod("ALIPAY")
                .paidAmount(new BigDecimal("9999.00"))
                .transactionNo("TXN-" + System.currentTimeMillis())
                .payerAccount("user@example.com")
                .paidAt(LocalDateTime.now())
                .paymentStatus("SUCCESS")
                .build();

        var result = orderEventPublisher.publishOrderPaid(event);

        assertTrue(result.isSuccess(), "Publish should succeed");
        log.info("Published OrderPaidEvent: {}", result.getTopic());

        Thread.sleep(2000);

        assertTrue(orderEventConsumer.hasReceivedPaidEvent(orderId));

        var receivedEvents = orderEventConsumer.getPaidEvents();
        var received = receivedEvents.stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .findFirst();

        assertTrue(received.isPresent());
        assertEquals("ALIPAY", received.get().getPaymentMethod());
        assertEquals("SUCCESS", received.get().getPaymentStatus());
    }

    @Test
    void testPublishAndConsumeOrderShippedEvent() throws Exception {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        OrderShippedEvent event = OrderShippedEvent.builder()
                .orderId(orderId)
                .shipmentId("SHIP-" + UUID.randomUUID().toString().substring(0, 8))
                .carrier("SF_EXPRESS")
                .carrierService("SF_STANDARD")
                .trackingNumber("SF" + System.currentTimeMillis())
                .receiverName("Zhang San")
                .receiverPhone("13800138000")
                .shippingAddress("Beijing, Chaoyang District, No.1")
                .shippedAt(LocalDateTime.now())
                .shipmentStatus("SHIPPED")
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(2).toString())
                .build();

        var result = orderEventPublisher.publishOrderShipped(event);

        assertTrue(result.isSuccess(), "Publish should succeed");
        log.info("Published OrderShippedEvent: {}", result.getTopic());

        Thread.sleep(2000);

        assertTrue(orderEventConsumer.hasReceivedShippedEvent(orderId));

        var receivedEvents = orderEventConsumer.getShippedEvents();
        var received = receivedEvents.stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .findFirst();

        assertTrue(received.isPresent());
        assertEquals("SF_EXPRESS", received.get().getCarrier());
    }

    @Test
    void testFullOrderLifecycle() throws Exception {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);

        log.info("=== Starting full order lifecycle test: {} ===", orderId);

        // Step 1: Create Order
        OrderCreatedEvent createdEvent = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId("USER-999")
                .productId("PROD-888")
                .productName("MacBook Pro M3")
                .quantity(1)
                .unitPrice(new BigDecimal("15999.00"))
                .totalAmount(new BigDecimal("15999.00"))
                .orderStatus("CREATED")
                .shippingAddress("Shanghai, Pudong District")
                .createdAt(LocalDateTime.now())
                .build();

        var createResult = orderEventPublisher.publishOrderCreated(createdEvent);
        assertTrue(createResult.isSuccess());
        log.info("Step 1: Order created, result={}", createResult.getTopic());

        Thread.sleep(3000);
        assertTrue(orderEventConsumer.hasReceivedCreatedEvent(orderId), "Should receive created event");

        // Step 2: Pay Order
        OrderPaidEvent paidEvent = OrderPaidEvent.builder()
                .orderId(orderId)
                .paymentId("PAY-" + System.currentTimeMillis())
                .paymentMethod("WECHAT_PAY")
                .paidAmount(new BigDecimal("15999.00"))
                .transactionNo("TXN-" + System.currentTimeMillis())
                .payerAccount("wechat-user")
                .paidAt(LocalDateTime.now())
                .paymentStatus("SUCCESS")
                .build();

        var payResult = orderEventPublisher.publishOrderPaid(paidEvent);
        assertTrue(payResult.isSuccess());
        log.info("Step 2: Order paid, result={}", payResult.getTopic());

        Thread.sleep(3000);
        assertTrue(orderEventConsumer.hasReceivedPaidEvent(orderId), "Should receive paid event");

        // Step 3: Ship Order
        OrderShippedEvent shippedEvent = OrderShippedEvent.builder()
                .orderId(orderId)
                .shipmentId("SHIP-" + System.currentTimeMillis())
                .carrier("JD_LOGISTICS")
                .carrierService("JD_EXPRESS")
                .trackingNumber("JD" + System.currentTimeMillis())
                .receiverName("Li Si")
                .receiverPhone("13900139000")
                .shippingAddress("Shanghai, Pudong District")
                .shippedAt(LocalDateTime.now())
                .shipmentStatus("SHIPPED")
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(1).toString())
                .build();

        var shipResult = orderEventPublisher.publishOrderShipped(shippedEvent);
        assertTrue(shipResult.isSuccess());
        log.info("Step 3: Order shipped, result={}", shipResult.getTopic());

        Thread.sleep(3000);
        assertTrue(orderEventConsumer.hasReceivedShippedEvent(orderId), "Should receive shipped event");

        log.info("=== Full lifecycle completed ===");
        assertTrue(orderEventConsumer.hasReceivedCreatedEvent(orderId));
        assertTrue(orderEventConsumer.hasReceivedPaidEvent(orderId));
        assertTrue(orderEventConsumer.hasReceivedShippedEvent(orderId));
    }

    private Integer getPartition(SendResult result) {
        if (result.getMetadata() instanceof Map<?, ?> meta) {
            return (Integer) meta.get("partition");
        }
        return null;
    }

    private Long getOffset(SendResult result) {
        if (result.getMetadata() instanceof Map<?, ?> meta) {
            return (Long) meta.get("offset");
        }
        return null;
    }
}