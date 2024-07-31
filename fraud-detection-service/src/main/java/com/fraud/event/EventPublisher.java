package com.fraud.event;

import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishAnomalyEvent(Transaction transaction, FraudRiskAssessment assessment, User user) {
        String message = String.format("Fraud detected: %s. User details: %s, %s.",
                assessment.getDetails(), user.getEmail(), user.getPhoneNumber());
        kafkaTemplate.send("fraud-events", message);
    }

}
