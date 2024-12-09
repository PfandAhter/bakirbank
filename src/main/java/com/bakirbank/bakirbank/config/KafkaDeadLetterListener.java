package com.bakirbank.bakirbank.config;

import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaDeadLetterListener {

    /*@KafkaListener(
            topics = "transfer-update.DLT", // Orijinal topic'in sonuna ".DLT" eklenir
            groupId = "dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void listenToUpdateTransfer(ConsumerRecord<String, String> record,
                                       @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stackTrace) {
        log.error("DLT Mesajı Alındı: {}", record.value());
        log.error("Hata Detayları: {}", stackTrace);
        // Gerekirse düzeltme veya analiz işlemleri
    }*/

    /*@KafkaListener(
            topics = "errorHandling.DLT", // Orijinal topic'in sonuna ".DLT" eklenir
            groupId = "dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void listenToFinalizeTransfer(ConsumerRecord<String, String> record,
                                         @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stackTrace) {
        log.error("DLT Mesajı Alındı: {}", record.value());
        log.error("Hata Detayları: {}", stackTrace);
        // Gerekirse düzeltme veya analiz işlemleri
    }*/

    @DltHandler
    public void listenDTL(MoneyTransferRequest moneyTransferRequest, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("DLT Received : {} , from {} , offset {}", moneyTransferRequest.getFromId(), topic, offset);
    }

}
