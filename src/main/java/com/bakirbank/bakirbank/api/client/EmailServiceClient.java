package com.bakirbank.bakirbank.api.client;


import com.bakirbank.bakirbank.api.request.SendNotificationRequest;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "EmailService" , url = "http://localhost:8085/email")
public interface EmailServiceClient {

    @PostMapping("/test")
    BaseResponse sendNotificationEmail(@RequestBody SendNotificationRequest sendNotificationRequest);


}
