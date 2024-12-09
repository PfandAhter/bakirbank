package com.bakirbank.bakirbank.api.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferMoneyResponse extends WithdrawMoneyResponse{


    public TransferMoneyResponse(String ownerName, Double balance) {
        super(ownerName, balance);
    }

}
