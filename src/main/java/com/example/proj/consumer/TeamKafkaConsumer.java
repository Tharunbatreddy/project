package com.example.proj.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TeamKafkaConsumer {

    // Create a Logger instance
    private static final Logger logger = LoggerFactory.getLogger(TeamKafkaConsumer.class);

    @KafkaListener(topics = "tasks", groupId = "team-group")
    public void consumeJobDetails(String jobDetails) {
        // Log the received job details
        logger.info("Received Job Details: {}", jobDetails);
        // You can also log other details or handle the job details as required
    }
}