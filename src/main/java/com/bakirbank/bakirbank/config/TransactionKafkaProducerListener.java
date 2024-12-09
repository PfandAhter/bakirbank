package com.bakirbank.bakirbank.config;

import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class TransactionKafkaProducerListener implements ProducerListener<String, MoneyTransferRequest> {

    @Override
    public void onSuccess(ProducerRecord<String, MoneyTransferRequest> producerRecord, RecordMetadata recordMetadata) {
        log.info("Message sent successfully to topic: " + recordMetadata.topic());
    }

    @Override
    public void onError(ProducerRecord<String, MoneyTransferRequest> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.error("Error while sending message to topic: " + recordMetadata.topic());
        //exception.printStackTrace();
    }

}
