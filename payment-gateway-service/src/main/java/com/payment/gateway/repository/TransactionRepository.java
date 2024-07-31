package com.payment.gateway.repository;


import com.payment.gateway.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveTransaction(Transaction transaction) {

        String sql = "INSERT INTO transaction (transaction_id,amount,transaction_date,account_id,currency,transaction_type,merchant_id,status,confirmation_details) " +
                "VALUES (?, ?, ?, ?, ?, ?,?,?,?)";


        jdbcTemplate.update(sql,
                new Object[]{
                        transaction.getTransactionId(),
                        transaction.getAmount(),
                        Timestamp.from(Instant.ofEpochMilli(transaction.getTimestamp())),
                        transaction.getAccountId(),
                        transaction.getCurrency(),
                        transaction.getTransactionType(),
                        transaction.getMerchant(),
                        transaction.getStatus(),
                        transaction.getConfirmationDetails()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.DECIMAL,
                        Types.TIMESTAMP,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR
                });
    }
}
