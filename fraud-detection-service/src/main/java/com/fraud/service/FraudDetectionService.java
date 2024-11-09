package com.fraud.service;

import com.fraud.exception.FraudDetetctionServiceException;
import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private TransactionService transactionService;

    @Cacheable("fraudRules")
    @Async
    public ListenableFuture<FraudRiskAssessment> analyseTransaction(Transaction transaction) {
        ListenableFutureTask<FraudRiskAssessment> task = new ListenableFutureTask<>(() -> {
            try {
                logger.debug("Analyzing transaction for fraud detection: {}", transaction);
                FraudRiskAssessment assessment = transactionService.getFraudRiskAssessment(transaction).get();
                logger.info("Fraud risk assessment completed for transaction ID: {}", transaction.getTransactionId());
                return assessment;
            } catch (Exception e) {
                logger.error("Error analyzing transaction for fraud detection: {}", transaction, e);
                throw new FraudDetetctionServiceException("Failed to analyze transaction for fraud detection", e);
            }
        });

        executorService.submit(task);
        return task;
    }
}
