package com.example.proj.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TeamKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TeamKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTeamName(String teamName) {
        kafkaTemplate.send("work", teamName);
    }
}
