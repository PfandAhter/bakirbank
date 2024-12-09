package com.bakirbank.bakirbank.rest.service;

import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.WithdrawMoneyResponse;
import com.bakirbank.bakirbank.model.Account;
import com.bakirbank.bakirbank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j

public class AccountProducerService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    private final KafkaTemplate<String,MoneyTransferRequest> moneyTransferKafkaTemplate;

    public void addMoney (String customerId, double amount){
        String message = String.format("%s %s", customerId, amount);
        kafkaTemplate.send("add-money", message);
        log.info("Islem alindi. Hesap numarasi: " + customerId + " miktar: " + amount);
    }

    public void withdrawMoney (String accountNumber, double amount){
        String message = String.format("%s %s", accountNumber, amount);
        kafkaTemplate.send("withdraw-money", message);

        log.info("Islem alindi. Hesap numarasi: " + accountNumber + " miktar: " + amount);
    }

    public void transferMoney(MoneyTransferRequest moneyTransferRequest){
        moneyTransferKafkaTemplate.send("transfer-start",moneyTransferRequest);
        log.info("Para transferi alindi. Gonderen hesap numarasi: " + moneyTransferRequest.getFromId() + " alici hesap numarasi: " + moneyTransferRequest.getToId() + " miktar: " + moneyTransferRequest.getAmount());
    }

}
