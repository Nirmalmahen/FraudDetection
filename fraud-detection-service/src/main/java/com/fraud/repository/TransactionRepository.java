package com.fraud.repository;

import com.fraud.exception.TransactionRepositoryException;
import com.fraud.mapper.TransactionRowMapper;
import com.fraud.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class TransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(TransactionRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public CompletableFuture<List<Transaction>> getTransactions() {
        String sql = "SELECT * FROM transactions";
        try {
            logger.debug("Executing query to fetch all transactions.");
            List<Transaction> transactions = jdbcTemplate.query(sql, new TransactionRowMapper());
            logger.info("Successfully fetched {} transactions.", transactions.size());
            return CompletableFuture.completedFuture(transactions);
        } catch (Exception e) {
            logger.error("Error fetching transactions from database", e);
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new TransactionRepositoryException("Failed to fetch transactions", e));
            return future;
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES (?, ?, ?, ?)";
        try {
            logger.debug("Executing query to save transaction for account ID: {}", transaction.getAccountId());
            jdbcTemplate.update(sql, transaction.getAccountId(), transaction.getAmount(),
                    transaction.getMerchant(), new Timestamp(transaction.getTimestamp()));
            logger.info("Successfully saved transaction for account ID: {}", transaction.getAccountId());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Error saving transaction for account ID: {}", transaction.getAccountId(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new TransactionRepositoryException("Failed to save transaction", e));
            return future;
        }
    }
}
