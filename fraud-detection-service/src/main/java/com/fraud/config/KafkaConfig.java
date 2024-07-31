package com.fraud.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private String bootStrapServer = "localhost:9092";

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProperty = new HashMap<>();
        configProperty.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        configProperty.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperty.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProperty);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProp = new HashMap<>();
        configProp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        configProp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProp.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-detection-group");
        return new DefaultKafkaConsumerFactory<>(configProp);
    }

//    @Bean
//    public ConcurrentMessageListenerContainer<String, String> messageListenerContainer() {
//        ConcurrentMessageListenerContainer<String, String> container =
//                new ConcurrentMessageListenerContainer<>(consumerFactory(), new ContainerProperties("fraud-events"));
//        container.setBeanName("fraud-events-container");
//        return container;
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
