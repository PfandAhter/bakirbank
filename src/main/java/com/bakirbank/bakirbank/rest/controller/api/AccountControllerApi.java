package com.bakirbank.bakirbank.rest.controller.api;

import com.bakirbank.bakirbank.api.request.CreateAccountRequest;
import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.AddMoneyResponse;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.api.response.TransferMoneyResponse;
import com.bakirbank.bakirbank.api.response.WithdrawMoneyResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface AccountControllerApi {

    @PostMapping("/create")
    ResponseEntity<BaseResponse> createAccount (@Valid @RequestBody CreateAccountRequest createAccountRequest);

    /*@PutMapping("/withdraw/{id}/{amount}")
    ResponseEntity<WithdrawMoneyResponse> withdrawMoney (@PathVariable("id") String userId, @PathVariable("amount")BigDecimal amount);*/

    /*@PostMapping("/withdraw")
    ResponseEntity<WithdrawMoneyResponse> testWithDrawMoney (@RequestParam("id") String userId, @RequestParam("amount") BigDecimal amount);*/

    @PostMapping("/withdraw")
    ResponseEntity<WithdrawMoneyResponse> withDrawMoney(@RequestParam("withdraw") List<String> withdrawParams);

    @PostMapping("/withdraw")
    ResponseEntity<BaseResponse> testWithDrawMoney(@RequestParam("withdraw") List<String> withdrawParams);

    @PostMapping("/add")
    ResponseEntity<AddMoneyResponse> addMoney(@RequestParam("add") List<String> addParams);

    @PostMapping("/transfer")
    ResponseEntity<TransferMoneyResponse> transferMoney(@Valid @RequestBody MoneyTransferRequest moneyTransferRequest);

}
