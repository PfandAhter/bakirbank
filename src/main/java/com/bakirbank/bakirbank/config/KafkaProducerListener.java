package com.bakirbank.bakirbank.config;

import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

public class KafkaProducerListener implements ProducerListener<String, MoneyTransferRequest> {

    @Override
    public void onSuccess(ProducerRecord<String, MoneyTransferRequest> producerRecord, RecordMetadata recordMetadata) {
        System.out.println("Message sent successfully to topic: " + recordMetadata.topic());
    }

    @Override
    public void onError(ProducerRecord<String, MoneyTransferRequest> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        System.err.println("Error while sending message to topic: " + recordMetadata.topic());
        //exception.printStackTrace();
    }

}
