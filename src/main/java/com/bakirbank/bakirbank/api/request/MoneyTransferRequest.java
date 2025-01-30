package com.bakirbank.bakirbank.api.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneyTransferRequest {

    private String fromId;
    private String fromAccountName;
    private String toId;
    private String toAccountName;
    private Double amount;
    private String description;

}