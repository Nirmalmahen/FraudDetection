package com.fraud.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    @KafkaListener(topics = "fraud-alerts", groupId = "fraud-detection-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record.value());
        // Add logic to handle the fraud alert message
    }
}
