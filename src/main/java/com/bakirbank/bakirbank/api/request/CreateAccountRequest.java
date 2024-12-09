package com.bakirbank.bakirbank.api.request;

import com.bakirbank.bakirbank.rest.validator.annotations.ValidAccountType;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidBranch;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidCurrency;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateAccountRequest {

    @NotBlank(message = "Customer id must not be null")
    private String customerId;

    @NotNull
    private Double balance;

    @NotNull(message = "Currency must not be null")
    @ValidCurrency
    private String currency;

    @NotNull(message = "Owner name must not be null")
    @ValidName
    private String ownerName;

    @NotNull(message = "Account type must not be null")
    @ValidAccountType
    private String accountType;

    @NotNull(message = "City must not be null")
    @ValidBranch
    private String branchCode;

}