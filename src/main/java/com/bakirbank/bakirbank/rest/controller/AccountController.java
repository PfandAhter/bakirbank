package com.bakirbank.bakirbank.rest.controller;

import com.bakirbank.bakirbank.api.request.CreateAccountRequest;
import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.AddMoneyResponse;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.api.response.TransferMoneyResponse;
import com.bakirbank.bakirbank.api.response.WithdrawMoneyResponse;
import com.bakirbank.bakirbank.rest.controller.api.AccountControllerApi;
import com.bakirbank.bakirbank.rest.service.AccountConsumerService;
import com.bakirbank.bakirbank.rest.service.AccountProducerService;
import io.micrometer.observation.Observation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/account")
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
    public ResponseEntity<WithdrawMoneyResponse> withDrawMoney(List<String> withdrawParams) {
        accountProducerService.withdrawMoney(withdrawParams.get(0), Double.parseDouble(withdrawParams.get(1)));


        return null;
    }

    @Override
    public ResponseEntity<BaseResponse> testWithDrawMoney(List<String> withdrawParams) {
        accountProducerService.withdrawMoney(withdrawParams.get(0), Double.parseDouble(withdrawParams.get(1)));

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setProcessMessage("Isleminiz basariyla tamamlandi.");
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }


    @Override
    public ResponseEntity<AddMoneyResponse> addMoney(List<String> addParams) {
        return null;
    }

    @Override
    public ResponseEntity<TransferMoneyResponse> transferMoney(MoneyTransferRequest moneyTransferRequest) {
        return null;
    }
}
