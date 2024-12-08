package com.bakirbank.bakirbank.rest.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {


    @KafkaListener(topics = "notification-service", groupId = "notification-group")
    public void sendNotification(String message){
        System.out.println("Notification sent: " + message); // TODO REFACTOR to logsf4j
    }

}
