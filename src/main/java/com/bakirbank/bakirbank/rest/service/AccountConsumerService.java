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
import com.bakirbank.bakirbank.model.enums.AccountType;
import com.bakirbank.bakirbank.model.enums.Currency;
import com.bakirbank.bakirbank.model.enums.TransactionType;
import com.bakirbank.bakirbank.repository.AccountRepository;
import com.bakirbank.bakirbank.repository.TransactionRepository;
import com.bakirbank.bakirbank.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.INSUFFICIENT_FUNDS;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.RECEIVER_NOT_FOUND;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.SENDER_NOT_FOUND;
import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.ACCOUNT_NOT_FOUND;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
                .accountNumber(Util.generateCode())
                .customerId(createAccountRequest.getCustomerId())
                .accountType(AccountType.valueOf(createAccountRequest.getAccountType()))
                .currency(Currency.valueOf(createAccountRequest.getCurrency()))
                .createdDate(LocalDateTime.now())
                .ownerName(createAccountRequest.getOwnerName())
                .balance(0.0)
                .transactionsAsReceiver(new ArrayList<>())
                .transactionsAsSender(new ArrayList<>())
                .lastUpdatedDate(LocalDateTime.now())
                .branchCode(createAccountRequest.getBranchCode())
                .accountType(AccountType.valueOf(createAccountRequest.getAccountType()))
                .status(AccountStatus.ACTIVE)
                .build());

        return new BaseResponse();
    }


    @KafkaListener(topics = "add-money", groupId = "money-group")
    public AddMoneyResponse addMoney(String message){
        try{
            String customerId = message.split(" ")[0];
            Double amount = Double.valueOf(message.split(" ")[1]);
            Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(customerId);
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
            String customerId = message.split(" ")[0];
            Double amount = Double.valueOf(message.split(" ")[1]);

            Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(customerId);
            Account account = accountOptional.orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_FOUND));

            if (account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                accountRepository.save(account);
            } else {
                log.info("Insufficient funds -> accountId " + customerId + " balance: " + account.getBalance() + " amount: " + amount);
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

    @KafkaListener(topics = "transfer-start", groupId = "transfer-group", containerFactory = "transactionKafkaListenerContainerFactory")
    public void processTransferStart(MoneyTransferRequest transferRequest) {
        try {
            Optional<Account> senderAccountOptional = accountRepository.findAccountByAccountNumber(transferRequest.getFromId());
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
            log.error("Error code: ", exception.getMessage());
        }
    }

    @KafkaListener(topics = "transfer-update", groupId = "transfer-group",containerFactory = "transactionKafkaListenerContainerFactory")
    public void processTransferUpdate(MoneyTransferRequest transferRequest, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset)throws NotFoundException{
        try {
            Optional<Account> receiverAccountOptional = accountRepository.findAccountByAccountNumber(transferRequest.getToId());
            Account receiverAccount = receiverAccountOptional.orElseThrow(() -> new NotFoundException(RECEIVER_NOT_FOUND));

            receiverAccount.setBalance(receiverAccount.getBalance() + transferRequest.getAmount());
            accountRepository.save(receiverAccount);

            // Kafka'ya finalize mesajı gönder
            moneyTransferKafkaTemplate.send("transfer-finalize", transferRequest);
        } catch (NotFoundException notFoundException) {
            log.error("Error: "+ "RECEIVER_NOT_FOUND" + notFoundException.getMessage());

            Optional<Account> senderAccountOptional = accountRepository.findAccountByAccountNumber(transferRequest.getFromId());
            senderAccountOptional.ifPresent(senderAccount -> {
                senderAccount.setBalance(senderAccount.getBalance() + transferRequest.getAmount());
                accountRepository.save(senderAccount);
            });
            //dltKafkaTemplate.send("transfer-update.DLT", notFoundException);

            throw new NotFoundException(notFoundException.getMessage());
        } catch (Exception exception) {
            log.error("Error code: ", exception.getMessage());
        }
    }

    @KafkaListener(topics = "transfer-finalize", groupId = "transfer-group", containerFactory = "transactionKafkaListenerContainerFactory")
    public void processTransferFinalize(MoneyTransferRequest transferRequest) {
        try {
            Optional<Account> senderAccountOptional = accountRepository.findAccountByAccountNumber(transferRequest.getFromId());
            Optional<Account> receiverAccountOptional = accountRepository.findAccountByAccountNumber(transferRequest.getToId());

            Account receiver = accountRepository.findAccountByAccountNumber(transferRequest.getToId()).orElseThrow(() -> new NotFoundException(RECEIVER_NOT_FOUND));
            Account sender = accountRepository.findAccountByAccountNumber(transferRequest.getFromId()).orElseThrow(() -> new NotFoundException(SENDER_NOT_FOUND));


            senderAccountOptional.ifPresent(senderAccount -> {
                String notificationMessage = String.format(
                        "Dear customer %s, your transfer of %.2f is successful. New balance: %.2f",
                        senderAccount.getId(),
                        transferRequest.getAmount(),
                        senderAccount.getBalance()
                );

                kafkaTemplate.send("notification-service", notificationMessage);
            });

            receiverAccountOptional.ifPresent(receiverAccount -> {
                String notificationMessage = String.format(
                        "Dear customer %s, you received %.2f from %s. New balance: %.2f",
                        receiverAccount.getId(),
                        transferRequest.getAmount(),
                        transferRequest.getFromId(),
                        receiverAccount.getBalance()
                );
                kafkaTemplate.send("notification-service", notificationMessage);
            });

            transactionRepository.save(Transaction.builder()
                    .senderAccount(sender)
                    .receiverAccount(receiver)
                    .transactionDate(LocalDateTime.now())
                    .description(transferRequest.getDescription())
                    .amount(transferRequest.getAmount())
                    .transactionType(TransactionType.TRANSFER).build());

        }catch (NotFoundException notFoundException){
            log.error("Error code: ", notFoundException.getMessage());
        }catch (Exception exception){
            log.error("Error code: ", exception.getMessage());
        }
    }
}