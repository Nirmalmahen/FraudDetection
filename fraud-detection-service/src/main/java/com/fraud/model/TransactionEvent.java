package com.fraud.model;

import lombok.Data;

@Data
public class TransactionEvent {
    private String transactionId;
    private EventType eventType; // e.g., "CREATED", "UPDATED", "HIGH_RISK_DETECTED"
    private Double amount;
    private String accountId;
}
