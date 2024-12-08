package com.bakirbank.bakirbank.rest.service;

import com.bakirbank.bakirbank.api.request.CreateAccountRequest;
import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import com.bakirbank.bakirbank.api.response.AddMoneyResponse;
import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.api.response.WithdrawMoneyResponse;
import com.bakirbank.bakirbank.exception.NotFoundException;
import com.bakirbank.bakirbank.model.Account;
import com.bakirbank.bakirbank.model.Transaction;
import com.bakirbank.bakirbank.model.enums.AccountStatus;
import com.bakirbank.bakirbank.model.enums.TransactionType;
import com.bakirbank.bakirbank.repository.AccountRepository;
import com.bakirbank.bakirbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.INSUFFICIENT_FUNDS;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.RECEIVER_NOT_FOUND;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.SENDER_NOT_FOUND;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.ACCOUNT_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j

public class AccountConsumerService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final KafkaTemplate<String, MoneyTransferRequest> moneyTransferKafkaTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;


    public BaseResponse createAccount(CreateAccountRequest createAccountRequest){
        accountRepository.save(Account.builder()
                .accountType(createAccountRequest.getAccountType())
                .currency(createAccountRequest.getCurrency())
                .createdDate(LocalDateTime.now())
                .ownerName(createAccountRequest.getOwnerName())
                .balance(0.0)
                .transactions(new ArrayList<>())
                .lastUpdatedDate(LocalDateTime.now())
                .accountType(createAccountRequest.getAccountType())
                .status(AccountStatus.ACTIVE)
                .build());

        return new BaseResponse();
    }


    @KafkaListener(topics = "add-money", groupId = "money-group")
    public AddMoneyResponse addMoney(String message){
        try{
            String accountNumber = message.split(" ")[0];
            Double amount = Double.valueOf(message.split(" ")[1]);
            Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(accountNumber);
            accountOptional.orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

            accountOptional.ifPresent(account ->{
                account.setBalance(account.getBalance() + amount);
                accountRepository.save(account);
            });

            return new AddMoneyResponse();
        }catch (Exception exception){
            log.error("Error: ", exception);
            return null;
        }
    }

    @KafkaListener(topics = "withdraw-money", groupId = "money-group")
    public WithdrawMoneyResponse withdrawMoney(String message) {
        try {
            String accountNumber = message.split(" ")[0];
            Double amount = Double.valueOf(message.split(" ")[1]);

            Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(accountNumber);
            Account account = accountOptional.orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

            if (account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                accountRepository.save(account);
            } else {
                log.info("Insufficient funds -> accountId" + accountNumber + " balance: " + account.getBalance() + " amount: " + amount);
                throw new NotFoundException(INSUFFICIENT_FUNDS);
            }

            return new WithdrawMoneyResponse(account.getOwnerName(), account.getBalance());
        } catch (NotFoundException notFoundException) {
            log.error("Error: ", "ACCOUNT_NOT_FOUND");
            return null;
        } catch (Exception exception) {
            log.error("Error: ", exception);
            return null; // TODO BURALARI DUZELT RETURN TIPLERIYLE ILGILI BILGI ALDIKTAN SONRA...
        }
    }

    @KafkaListener(topics = "transfer-start", groupId = "transfer-group")
    public void processTransferStart(MoneyTransferRequest transferRequest) {
        try {
            Optional<Account> senderAccountOptional = accountRepository.findById(transferRequest.getFromId());
            Account senderAccount = senderAccountOptional.orElseThrow(() -> new NotFoundException(SENDER_NOT_FOUND));

            if (senderAccount.getBalance() >= transferRequest.getAmount()) {
                senderAccount.setBalance(senderAccount.getBalance() - transferRequest.getAmount());
                accountRepository.save(senderAccount);

                // Kafka'ya mesaj gönder
                moneyTransferKafkaTemplate.send("transfer-update", transferRequest);
            } else {
                log.info("Insufficient funds -> accountId: " + transferRequest.getFromId());
                throw new NotFoundException(INSUFFICIENT_FUNDS);
            }
        } catch (Exception exception) {
            log.error("Error: ", exception);
        }
    }

    @KafkaListener(topics = "transfer-update", groupId = "transfer-group")
    public void processTransferUpdate(MoneyTransferRequest transferRequest) {
        try {
            Optional<Account> receiverAccountOptional = accountRepository.findById(transferRequest.getToId());
            Account receiverAccount = receiverAccountOptional.orElseThrow(() -> new NotFoundException(RECEIVER_NOT_FOUND));

            receiverAccount.setBalance(receiverAccount.getBalance() + transferRequest.getAmount());
            accountRepository.save(receiverAccount);

            // Kafka'ya finalize mesajı gönder
            moneyTransferKafkaTemplate.send("transfer-finalize", transferRequest);
        } catch (NotFoundException notFoundException) {
            log.error("Error: ", "RECEIVER_NOT_FOUND");

            Optional<Account> senderAccountOptional = accountRepository.findById(transferRequest.getFromId());
            senderAccountOptional.ifPresent(senderAccount -> {
                senderAccount.setBalance(senderAccount.getBalance() + transferRequest.getAmount());
                accountRepository.save(senderAccount);
            });
        } catch (Exception exception) {
            log.error("Error: ", exception);
        }
    }

    @KafkaListener(topics = "transfer-finalize", groupId = "transfer-group")
    public void processTransferFinalize(MoneyTransferRequest transferRequest) {
        Optional<Account> senderAccountOptional = accountRepository.findById(transferRequest.getFromId());
        Optional<Account> receiverAccountOptional = accountRepository.findById(transferRequest.getToId());

        senderAccountOptional.ifPresent(senderAccount -> {
            String notificationMessage = String.format(
                    "Dear customer %s, your transfer of %.2f is successful. New balance: %.2f",
                    senderAccount.getId(),
                    transferRequest.getAmount(),
                    senderAccount.getBalance()
            );

            transactionRepository.save(Transaction.builder()
                    .account(senderAccountOptional.get())
                    .transactionDate(LocalDateTime.now())
                    .description(transferRequest.getDescription())
                    .amount(transferRequest.getAmount())
                    .transactionType(TransactionType.TRANSFER).build());

            transactionRepository.save(Transaction.builder()
                    .account(receiverAccountOptional.get())
                    .transactionDate(LocalDateTime.now())
                    .description(transferRequest.getDescription())
                    .amount(transferRequest.getAmount())
                    .transactionType(TransactionType.TRANSFER).build());

            kafkaTemplate.send("transfer-notification", notificationMessage);
        });

        receiverAccountOptional.ifPresent(receiverAccount -> {
            String notificationMessage = String.format(
                    "Dear customer %s, you received %.2f from %s. New balance: %.2f",
                    receiverAccount.getId(),
                    transferRequest.getAmount(),
                    transferRequest.getFromId(),
                    receiverAccount.getBalance()
            );
            kafkaTemplate.send("transfer-notification", notificationMessage);
        });
    }
}