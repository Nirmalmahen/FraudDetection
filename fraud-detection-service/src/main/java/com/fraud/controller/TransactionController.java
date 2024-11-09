package com.fraud.controller;

import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/fraud-detection")
@Tag(name = "Fraud Detection API", description = "API for fraud detection microservice")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @PostMapping("/checkTransaction")
    @Operation(summary = "Check Transaction for Fraud", description = "Receives transaction data for analysis and returns fraud risk assessment.")
    @Async
    public CompletableFuture<ResponseEntity<FraudRiskAssessment>> checkTransaction(@Valid @RequestBody Transaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Received transaction for fraud check: {}", transaction);
                FraudRiskAssessment assessment = fraudDetectionService.analyseTransaction(transaction).get();
                logger.info("Fraud risk assessment completed for transaction ID: {}", transaction.getTransactionId());
                return ResponseEntity.ok(assessment);
            } catch (Exception e) {
                logger.error("Error processing transaction ID: {}", transaction.getTransactionId(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        });
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
    }
}
