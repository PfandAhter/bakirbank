package com.bakirbank.bakirbank.rest.service.impl;

import com.bakirbank.bakirbank.api.client.NotificationServiceClient;
import com.bakirbank.bakirbank.api.request.MoneyProcessFailed;
import com.bakirbank.bakirbank.api.request.SendNotificationRequest;
import com.bakirbank.bakirbank.lib.ResponseStatus;
import com.bakirbank.bakirbank.model.ErrorCodes;
import com.bakirbank.bakirbank.rest.service.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


//@Service
@RequiredArgsConstructor
@Component
@Slf4j
public class DLTKafkaTemplate {

    private final ICacheService cacheService;

    //private final NotificationServiceClient notificationServiceClient;

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
    public void listenDTL(Exception exception, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        //log.info("DLT Received : {} , from {} , offset {}", moneyTransferRequest.getFromId(), topic, offset);
        log.info("TEST HATA HATA HATA");
    }

    @KafkaListener(
            topics = "money-process.DLT",
            groupId = "dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void listenToErrorHandling(ConsumerRecord<String,MoneyProcessFailed> moneyProcessRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        try {
//            MoneyProcessFailed moneyProcessFailed = moneyProcessRecord.value();
            String test2 = moneyProcessRecord.value().getBankName();

            MoneyProcessFailed test = moneyProcessRecord.value();

//            log.error("DLT Received : {} , from {} , offset {}", moneyProcessFailed, topic, offset);
//            String error = moneyProcessFailed.toString().split("]")[1].split(":")[1].split(",")[0].substring(1, 6);
            String error = "ERROR";
            ErrorCodes errorCode = cacheService.getErrorCodesList().get(error);

            if (errorCode ==   null) {
                errorCode = new ErrorCodes();
                errorCode.setId(ResponseStatus.FAILED);
                errorCode.setError(error);
                errorCode.setDescription("Undefined error code : " + error);
            }


            /*notificationServiceClient.sendNotificationEmail(
                    SendNotificationRequest.builder()

            );*/


            log.info("The transaction failed with the error description : {}", errorCode.getDescription());

        }catch (Exception exception1){
            log.error("Error while handling DLT message : {}", exception1.getMessage());
        }
    }


    /*@KafkaListener(topics = "transfer-update.DLT", groupId = "dlt-group")
    public void dltHandle (Exception exception){
        log.error("DLT HANDLE EXCPETION CALISTI...");
    }*/
}