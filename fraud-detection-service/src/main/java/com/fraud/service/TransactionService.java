package com.fraud.service;

import com.fraud.event.EventPublisher;
import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.model.User;
import com.fraud.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private static final double HIGH_RISK_AMOUNT_THRESHOLD = 10000.0;
    private static final long SHORT_TIME_WINDOW = Duration.ofSeconds(30).toMillis();

    private Map<String, Long> lastTransactionTimeMap = new HashMap<>();

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RuleCacheService ruleCacheService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private UserService userService;

    public List<Transaction> getALlTransactions() {
        return transactionRepository.getTransactions();
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }

    public FraudRiskAssessment getFraudRiskAssessment(Transaction transaction) {
        FraudRiskAssessment assessment = new FraudRiskAssessment();
        assessment.setRiskLevel("LOW");

        if (transaction.getAmount() > HIGH_RISK_AMOUNT_THRESHOLD) {
            assessment.setRiskLevel("HIGH");
            assessment.setDetails("Transaction amount exceeds threshold");
            User user = userService.getUserByAccountId(transaction.getAccountId());
            eventPublisher.publishAnomalyEvent(transaction, assessment, user);
            return assessment;
        }

        String restrictedMerchant = ruleCacheService.getRule("restrictedMerchant");
        if (transaction.getMerchant().equalsIgnoreCase(restrictedMerchant)) {
            assessment.setRiskLevel("HIGH");
            assessment.setDetails("Transaction is from restricted Merchant");
            User user = userService.getUserByAccountId(transaction.getAccountId());
            eventPublisher.publishAnomalyEvent(transaction, assessment, user);
            return assessment;
        }

        Long lastTransactionTime = lastTransactionTimeMap.get(transaction.getAccountId());

        if (lastTransactionTime != null) {
            long duration = transaction.getTimestamp() - lastTransactionTime;

            if (duration < SHORT_TIME_WINDOW) {
                assessment.setRiskLevel("MEDIUM");
                assessment.setDetails("Multiple transactions in a short time window.");
                User user = userService.getUserByAccountId(transaction.getAccountId());
                eventPublisher.publishAnomalyEvent(transaction, assessment, user);
                return assessment;
            }
        }
        lastTransactionTimeMap.put(transaction.getAccountId(), transaction.getTimestamp());

        LocalDateTime transactionTime = new Timestamp(transaction.getTimestamp()).toLocalDateTime();
        int startHour = ruleCacheService.getRule("businessStartHour");
        int endHour = ruleCacheService.getRule("businessEndHour");
        if (transactionTime.getHour() < startHour || transactionTime.getHour() > endHour) {
            assessment.setRiskLevel("MEDIUM");
            assessment.setDetails("Transaction outside business hours.");
            User user = userService.getUserByAccountId(transaction.getAccountId());
            eventPublisher.publishAnomalyEvent(transaction, assessment, user);
            return assessment;
        }

        return assessment;
    }

}
