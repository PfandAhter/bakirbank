package com.bakirbank.bakirbank.rest.controller.api;

import com.bakirbank.bakirbank.api.request.CreateAccountRequest;
import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.AddMoneyResponse;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.api.response.TransferMoneyResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AccountControllerApi {

    @PostMapping(path = "/create",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BaseResponse> createAccount (@Valid @RequestBody CreateAccountRequest createAccountRequest);

    /*@PutMapping("/withdraw/{id}/{amount}")
    ResponseEntity<WithdrawMoneyResponse> withdrawMoney (@PathVariable("id") String userId, @PathVariable("amount")BigDecimal amount);*/

    /*@PostMapping(path = "/withdraw")
    ResponseEntity<BaseResponse> testWithDrawMoney(@RequestParam("withdraw") List<String> withdrawParams);*/

    @PostMapping(path = "/withdraw")
    ResponseEntity<BaseResponse> testWithDrawMoney(@RequestParam("customerId") String customerId, @RequestParam("amount") String amount);

    @PostMapping(path = "/add")
    ResponseEntity<BaseResponse> addMoney(@RequestParam("customerId") String customerId, @RequestParam("amount") String amount);

    @PostMapping(path = "/transfer")
    ResponseEntity<BaseResponse> transferMoney(@Valid @RequestBody MoneyTransferRequest moneyTransferRequest);

}
