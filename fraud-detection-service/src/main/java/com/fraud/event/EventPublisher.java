package com.fraud.event;

import com.fraud.model.FraudRiskAssessment;
import com.fraud.model.Transaction;
import com.fraud.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private static final int THREAD_POOL_SIZE = 10; // Adjust based on expected concurrency level

    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public void publishAnomalyEvent(Transaction transaction, FraudRiskAssessment assessment, User user) {
        executorService.submit(() -> {
            String message = String.format("Fraud detected: %s. User details: %s, %s.",
                    assessment.getDetails(), user.getEmail(), user.getPhoneNumber());

            logger.info("Publishing anomaly event for transaction ID: {}", transaction.getTransactionId());

            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("fraud-events", message);
            kafkaTemplate.flush();

            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    logger.info("Successfully published anomaly event for transaction ID: {} with offset: {}",
                            transaction.getTransactionId(), result.getRecordMetadata().offset());
                }
                @Override
                public void onFailure(Throwable ex) {
                    logger.error("Failed to publish anomaly event for transaction ID: {}", transaction.getTransactionId(), ex);
                }
            });
        });
    }
}
