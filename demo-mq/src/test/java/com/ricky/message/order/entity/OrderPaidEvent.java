package com.ricky.message.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String paymentId;
    private String paymentMethod;
    private BigDecimal paidAmount;
    private String transactionNo;
    private String payerAccount;
    private LocalDateTime paidAt;
    private String paymentStatus;

    @Override
    public String toString() {
        return "OrderPaidEvent{" +
                "orderId='" + orderId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paidAmount=" + paidAmount +
                ", transactionNo='" + transactionNo + '\'' +
                ", payerAccount='" + payerAccount + '\'' +
                ", paidAt=" + paidAt +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}