package com.bakirbank.bakirbank.rest.controller;

import com.bakirbank.bakirbank.api.request.CreateAccountRequest;
import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.AddMoneyResponse;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.api.response.TransferMoneyResponse;
import com.bakirbank.bakirbank.rest.controller.api.AccountControllerApi;
import com.bakirbank.bakirbank.rest.service.AccountConsumerService;
import com.bakirbank.bakirbank.rest.service.AccountProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/account")
@CrossOrigin
@RequiredArgsConstructor

public class AccountController implements AccountControllerApi {

    private final AccountProducerService accountProducerService;

    private final AccountConsumerService accountConsumerService;

    @Override
    public ResponseEntity<BaseResponse> createAccount(CreateAccountRequest createAccountRequest) {
        return ResponseEntity.ok(accountConsumerService.createAccount(createAccountRequest));
    }

    @Override
    public ResponseEntity<BaseResponse> testWithDrawMoney(String customerId, String amount) {
        accountProducerService.withdrawMoney(customerId, Double.parseDouble(amount));

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setProcessMessage("Isleminiz basariyla alindi.");
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse> addMoney(String customerId, String amount) {
        accountProducerService.addMoney(customerId,Double.parseDouble(amount));

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setProcessMessage("Isleminiz basariyla alindi.");
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }


    @Override
    public ResponseEntity<BaseResponse> transferMoney(MoneyTransferRequest moneyTransferRequest) {
        accountProducerService.transferMoney(moneyTransferRequest);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setProcessMessage("Isleminiz basariyla alindi.");
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
