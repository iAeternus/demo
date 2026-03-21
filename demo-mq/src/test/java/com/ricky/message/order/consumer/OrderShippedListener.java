package com.ricky.message.order.consumer;

import com.ricky.message.Message;
import com.ricky.message.MessageConsumer;
import com.ricky.message.annotation.EventListener;
import com.ricky.message.order.entity.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@EventListener(topic = "order-shipped")
public class OrderShippedListener implements MessageConsumer<OrderShippedEvent> {

    private static final Logger log = LoggerFactory.getLogger(OrderShippedListener.class);

    @Override
    public void onMessage(Message<OrderShippedEvent> message) throws Exception {
        OrderShippedEvent event = message.getPayload();
        log.info("Received OrderShippedEvent: orderId={}, carrier={}, trackingNumber={}",
                event.getOrderId(), event.getCarrier(), event.getTrackingNumber());
    }
}