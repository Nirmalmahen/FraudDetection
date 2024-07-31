package com.payment.gateway.utils;

import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class TransactionLogger {

    /**
     * Logs transaction details to the console or a persistent storage system.
     *
     * @param request  The payment request containing transaction details.
     * @param response status The transaction status indicating success or failure.
     * @param response confirmationDetails Additional confirmation details for the transaction.
     */
    public void logTransaction(PaymentRequest request, PaymentResponse response) {
        System.out.println("Transaction Log:");
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Payment Method: " + request.getPaymentMethod());
        System.out.println("User Details: " + request.getAccountId());
        System.out.println("Status: " + response.getStatus());
        System.out.println("Confirmation Details: " + response.getConfirmationDetails());
        System.out.println("------------------------------");
    }
}
