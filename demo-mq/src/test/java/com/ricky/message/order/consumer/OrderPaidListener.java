package com.ricky.message.order.consumer;

import com.ricky.message.Message;
import com.ricky.message.MessageConsumer;
import com.ricky.message.annotation.EventListener;
import com.ricky.message.order.entity.OrderPaidEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@EventListener(topic = "order-paid")
public class OrderPaidListener implements MessageConsumer<OrderPaidEvent> {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidListener.class);

    @Override
    public void onMessage(Message<OrderPaidEvent> message) throws Exception {
        OrderPaidEvent event = message.getPayload();
        log.info("Received OrderPaidEvent: orderId={}, paymentId={}, amount={}, status={}",
                event.getOrderId(), event.getPaymentId(), event.getPaidAmount(), event.getPaymentStatus());
    }
}