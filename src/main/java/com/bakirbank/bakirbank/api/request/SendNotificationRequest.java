package com.bakirbank.bakirbank.api.request;

import com.bakirbank.bakirbank.model.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class SendNotificationRequest implements Serializable {

    private String customerId;

    private TransactionType transactionType;

    private String bankName;

    private Double amount;

    private String message;
}