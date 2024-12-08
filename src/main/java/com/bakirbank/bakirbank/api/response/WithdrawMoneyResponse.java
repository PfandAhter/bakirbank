package com.bakirbank.bakirbank.api.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class WithdrawMoneyResponse extends BaseResponse{

    @JsonProperty("Account owner name")
    private String ownerName;

    @JsonProperty("balance")
    private Double balance;

}