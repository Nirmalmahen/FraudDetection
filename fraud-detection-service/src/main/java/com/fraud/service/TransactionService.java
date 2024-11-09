package com.fraud.service;

import com.fraud.event.EventPublisher;
import com.fraud.exception.TransactionServiceException;
import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.model.User;
import com.fraud.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();


    @Async
    public CompletableFuture<List<Transaction>> getAllTransactions() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Fetching all transactions from the database.");
                List<Transaction> transactions = transactionRepository.getTransactions().get();
                logger.info("Successfully fetched {} transactions.", transactions.size());
                return transactions;
            } catch (Exception e) {
                logger.error("Failed to fetch transactions", e);
                throw new TransactionServiceException("Error retrieving transactions from the database", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<Void> saveTransaction(Transaction transaction) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.debug("Saving transaction with ID: {}", transaction.getTransactionId());
                transactionRepository.saveTransaction(transaction);
                logger.info("Transaction successfully saved with ID: {}", transaction.getTransactionId());
            } catch (Exception e) {
                logger.error("Failed to save transaction with ID: {}", transaction.getTransactionId(), e);
                throw new TransactionServiceException("Error saving transaction to the database", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<FraudRiskAssessment> getFraudRiskAssessment(Transaction transaction) {
        return CompletableFuture.supplyAsync(()-> {
            try {
                logger.debug("Starting fraud risk assessment for transaction ID: {}", transaction.getTransactionId());
                FraudRiskAssessment assessment = new FraudRiskAssessment();
                assessment.setRiskLevel("LOW");

                if (transaction.getAmount() > HIGH_RISK_AMOUNT_THRESHOLD) {
                    assessment.setRiskLevel("HIGH");
                    assessment.setDetails("Transaction amount exceeds threshold");
                    User user = userService.getUserByAccountId(transaction.getAccountId()).get();
                    eventPublisher.publishAnomalyEvent(transaction, assessment, user);
                    logger.info("High risk transaction detected for transaction ID: {}", transaction.getTransactionId());
                    return assessment;
                }

                String restrictedMerchant = getCachedRule("restrictedMerchant").get();
                if (transaction.getMerchant().equalsIgnoreCase(restrictedMerchant)) {
                    assessment.setRiskLevel("HIGH");
                    assessment.setDetails("Transaction is from restricted Merchant");
                    User user = userService.getUserByAccountId(transaction.getAccountId()).get();
                    eventPublisher.publishAnomalyEvent(transaction, assessment, user);
                    logger.info("Transaction from restricted merchant detected for transaction ID: {}", transaction.getTransactionId());
                    return assessment;
                }

                LocalDateTime transactionTime = new Timestamp(transaction.getTimestamp()).toLocalDateTime();
                int startHour = Integer.parseInt(getCachedRule("businessStartHour").get());
                int endHour = Integer.parseInt(getCachedRule("businessEndHour").get());
                if (transactionTime.getHour() < startHour || transactionTime.getHour() > endHour) {
                    assessment.setRiskLevel("MEDIUM");
                    assessment.setDetails("Transaction outside business hours.");
                    User user = userService.getUserByAccountId(transaction.getAccountId()).get();
                    eventPublisher.publishAnomalyEvent(transaction, assessment, user);
                    logger.info("Transaction outside business hours detected for transaction ID: {}", transaction.getTransactionId());
                    return assessment;
                }

                logger.info("Transaction assessed as low risk for transaction ID: {}", transaction.getTransactionId());
                return assessment;
            } catch (Exception e) {
                logger.error("Error assessing fraud risk for transaction ID: {}", transaction.getTransactionId(), e);
                throw new TransactionServiceException("Failed to assess fraud risk for transaction", e);
            }
        }, executorService);
    }

    @Cacheable(value = "rules", key = "#ruleKey")
    @Async
    public CompletableFuture<String> getCachedRule(String ruleKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Attempting to fetch rule with key: {}", ruleKey);
                String rule = String.valueOf(ruleCacheService.getRule(ruleKey).get());
                if (rule == null) {
                    logger.warn("No rule found for key: {}", ruleKey);
                } else {
                    logger.info("Successfully retrieved rule for key: {}", ruleKey);
                }
                return rule;
            } catch (Exception e) {
                logger.error("Failed to fetch rule with key: {}", ruleKey, e);
                throw new TransactionServiceException("Error retrieving rule from cache", e);
            }
        }, executorService);
    }
}
