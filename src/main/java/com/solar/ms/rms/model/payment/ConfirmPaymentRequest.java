package com.solar.ms.rms.model.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmPaymentRequest {
    private String transactionId;
    private Double amount;
}
