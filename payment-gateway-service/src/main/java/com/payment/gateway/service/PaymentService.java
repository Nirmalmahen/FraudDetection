package com.payment.gateway.service;


import com.payment.gateway.enums.TransactionStatus;
import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.User;
import com.payment.gateway.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    public PaymentResponse processPayment(User user, PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        if (request.getAmount() <= user.getBalance()) {
            response.setStatus(TransactionStatus.SUCCESS);
            response.setConfirmationDetails("Transaction ID: " + request.getTransactionId() + ", Amount: " + request.getAmount() + " " + request.getCurrency());
        } else {
            response.setStatus(TransactionStatus.FAILURE);
            response.setConfirmationDetails("Insufficient Balance for Transaction ID: " + request.getTransactionId());
        }

        return response;
    }

}
