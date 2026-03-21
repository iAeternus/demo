package com.ricky.message.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String shipmentId;
    private String carrier;
    private String carrierService;
    private String trackingNumber;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private LocalDateTime shippedAt;
    private String shipmentStatus;
    private String estimatedDeliveryTime;

    @Override
    public String toString() {
        return "OrderShippedEvent{" +
                "orderId='" + orderId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", carrier='" + carrier + '\'' +
                ", carrierService='" + carrierService + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", shippedAt=" + shippedAt +
                ", shipmentStatus='" + shipmentStatus + '\'' +
                '}';
    }
}