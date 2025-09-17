package com.example.proj.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Renaming the logger variable to avoid conflicts
    private static final Logger kafkaLogger = LoggerFactory.getLogger(KafkaConfig.class);

    // Producer configuration bean
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");  // Adjust Kafka server URL
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(producerProps);
        return new KafkaTemplate<>(factory);
    }

    // Consumer configuration bean
    @Bean
    public MessageListenerContainer messageListenerContainer() {
        // Consumer properties for the Kafka listener
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");  // Adjust Kafka server URL
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "proj-consumer-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Create the ConsumerFactory
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);

        // Set up container properties
        ContainerProperties containerProps = new ContainerProperties("teams");  // Set the Kafka topic

        // Use a lambda expression for the message listener
        MessageListener<String, String> messageListener = (ConsumerRecord<String, String> consumerRecord) -> {
            // Logic for processing the incoming message
            String teamName = consumerRecord.value();
            kafkaLogger.info("Received team name from Kafka: {}", teamName);

            // Process the message, e.g., matching it with job data or sending it to another service
            // Implement your job matching logic here...
        };

        // Set the message listener to container properties
        containerProps.setMessageListener(messageListener);

        // Create and return the KafkaMessageListenerContainer
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    }
}