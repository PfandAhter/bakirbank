package com.bakirbank.bakirbank.api.request;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneyTransferRequest {

    private String fromId;
    private String toId;
    private Double amount;
    private String description;

}