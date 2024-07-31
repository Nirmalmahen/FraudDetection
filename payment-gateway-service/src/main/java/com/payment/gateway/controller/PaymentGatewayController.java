package com.payment.gateway.controller;


import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.service.PaymentProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payment Gateway API", description = "API for Payment Gateway microservice")
public class PaymentGatewayController {

    @Autowired
    private PaymentProcessor paymentProcessor;

//    {
//        "amount": 100,
//            "currency": "EUR",
//            "paymentMethod": "Credit Card",
//            "userDetails": "User1"
//    }


    @PostMapping("/processPayment")
    @Operation(summary = "Process payments", description = "Process Payments.")
    public PaymentResponse processPayment(@RequestBody @Valid PaymentRequest request) {
        return paymentProcessor.processPayment(request);
    }
}
