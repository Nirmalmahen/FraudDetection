package com.payment.gateway.event;

import com.payment.gateway.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    public void publishTransaction(Transaction transaction) {
        String key = transaction.getTransactionId();
        kafkaTemplate.send("transaction-events", key, transaction);
    }

}
