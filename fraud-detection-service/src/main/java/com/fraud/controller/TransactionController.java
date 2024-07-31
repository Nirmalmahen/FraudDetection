package com.fraud.controller;

import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/fraud-detection")
@Tag(name = "Fraud Detection API", description = "API for fraud detection microservice")
public class TransactionController {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @PostMapping("/checkTransaction")
    @Operation(summary = "Check Transaction for Fraud", description = "Receives transaction data for analysis and returns fraud risk assessment.")
    public FraudRiskAssessment checkTransaction(@Valid @RequestBody Transaction transaction) {
        return fraudDetectionService.analyseTransaction(transaction);
    }

}
