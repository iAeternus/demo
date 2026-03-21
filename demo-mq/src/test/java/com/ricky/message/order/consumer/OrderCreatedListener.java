package com.ricky.message.order.consumer;

import com.ricky.message.Message;
import com.ricky.message.MessageConsumer;
import com.ricky.message.annotation.EventListener;
import com.ricky.message.order.entity.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@EventListener(topic = "order-created")
public class OrderCreatedListener implements MessageConsumer<OrderCreatedEvent> {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @Override
    public void onMessage(Message<OrderCreatedEvent> message) throws Exception {
        OrderCreatedEvent event = message.getPayload();
        log.info("Received OrderCreatedEvent: orderId={}, userId={}, amount={}, status={}",
                event.getOrderId(), event.getUserId(), event.getTotalAmount(), event.getOrderStatus());
    }
}