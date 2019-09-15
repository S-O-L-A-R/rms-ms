package com.solar.ms.rms.model.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservePaymentRequest {
    private String productName;
    private BigDecimal amount;
    private String orderId;
    private String confirmUrl;
}
