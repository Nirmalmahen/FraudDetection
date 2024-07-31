package com.fraud.service;

import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {

    @Autowired
    private TransactionService transactionService;

    @Cacheable("fraudRules")
    public FraudRiskAssessment analyseTransaction(Transaction transaction) {
        return transactionService.getFraudRiskAssessment(transaction);
    }

}
