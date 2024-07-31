package com.payment.gateway.model;


import com.payment.gateway.enums.Currency;
import com.payment.gateway.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "TransactionID cannot be null")
    private String transactionId;
    @NotNull(message = "Amount cannot be empty")
    private double amount;
    @NotNull(message = "currency cannot be empty")
    private Currency currency;
    @NotNull(message = "paymentMethod cannot be empty")
    private PaymentMethod paymentMethod;
    @NotNull(message = "merchantId cannot be empty")
    private String merchantId;
    @NotNull(message = "accountId cannot be empty")
    private String accountId;
}
