package com.fraud.mapper;

import com.fraud.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getString("id"));
        transaction.setAccountId(rs.getString("account_id"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setMerchant(rs.getString("merchant"));
        transaction.setTimestamp(rs.getTimestamp("transaction_date").getTime());
        return transaction;
    }
}