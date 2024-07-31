package com.fraud.repository;

import com.fraud.mapper.TransactionRowMapper;
import com.fraud.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Transaction> getTransactions() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, new TransactionRowMapper());
    }

    public void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, amount, merchant, transaction_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getAccountId(), transaction.getAmount(),
                transaction.getMerchant(), new Timestamp(transaction.getTimestamp()));
    }
}
