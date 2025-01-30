package com.bakirbank.bakirbank.api.client;


import com.bakirbank.bakirbank.api.request.SendNotificationRequest;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "NotificationService" , url = "http://localhost:8090/testing") //TODO REFACTOR Port number and NotificationService is the name of the service that we want to call
public interface NotificationServiceClient {

    @PostMapping("/test") //TODO refactor the endpoint
    BaseResponse sendNotificationEmail(@RequestBody SendNotificationRequest sendNotificationRequest);

}