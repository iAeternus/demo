package com.ricky.message.order;

import com.ricky.message.MessageTemplate;
import com.ricky.message.order.entity.OrderCreatedEvent;
import com.ricky.message.order.entity.OrderPaidEvent;
import com.ricky.message.order.entity.OrderShippedEvent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.enable-auto-commit=true",
        "ricky.messaging.broker=kafka",
        "ricky.messaging.kafka.topics=order-created,order-paid,order-shipped"
})
public class OrderEventIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(OrderEventIntegrationTest.class);

    @Autowired
    private MessageTemplate messageTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void test_publish_and_consume_order_created_event() throws Exception {
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

        messageTemplate.send("order-created", event);

        log.info("Published OrderCreatedEvent: orderId={}", orderId);

        Thread.sleep(2000);

        assertNotNull(event.getOrderId());
    }

    @Test
    void test_publish_and_consume_order_paid_event() throws Exception {
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

        messageTemplate.send("order-paid", event);

        log.info("Published OrderPaidEvent: orderId={}", orderId);

        Thread.sleep(2000);

        assertNotNull(event.getOrderId());
    }

    @Test
    void test_publish_and_consume_order_shipped_event() throws Exception {
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

        messageTemplate.send("order-shipped", event);

        log.info("Published OrderShippedEvent: orderId={}", orderId);

        Thread.sleep(2000);

        assertNotNull(event.getOrderId());
    }

    @Test
    void test_full_order_lifecycle() throws Exception {
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

        messageTemplate.send("order-created", createdEvent);
        log.info("Step 1: Order created");

        Thread.sleep(3000);

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

        messageTemplate.send("order-paid", paidEvent);
        log.info("Step 2: Order paid");

        Thread.sleep(3000);

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

        messageTemplate.send("order-shipped", shippedEvent);
        log.info("Step 3: Order shipped");

        Thread.sleep(3000);

        log.info("=== Full lifecycle completed ===");
    }
}