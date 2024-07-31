package com.payment.gateway.service;

import com.payment.gateway.enums.TransactionStatus;
import com.payment.gateway.model.PaymentRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ThirdPartyProviderIntegration {

    /**
     * Processes a PayPal payment through a simulated third-party provider.
     *
     * @param amount  The amount to be processed.
     * @param request The payment request containing transaction details.
     * @return The transaction status indicating success or failure.
     */
    public TransactionStatus processPayPalPayment(BigDecimal amount, PaymentRequest request) {
        // Simulated integration with PayPal or similar third-party provider
        System.out.println("Processing PayPal payment for amount: " + amount + " " + request.getCurrency());
        return TransactionStatus.SUCCESS; // Placeholder for real integration
    }
}
