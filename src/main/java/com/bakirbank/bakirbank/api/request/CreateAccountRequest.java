package com.bakirbank.bakirbank.api.request;

import com.bakirbank.bakirbank.model.Branch;
import com.bakirbank.bakirbank.model.enums.AccountStatus;
import com.bakirbank.bakirbank.model.enums.AccountType;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidAccountType;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidBranch;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateAccountRequest extends BaseAccountRequest {

    @NotNull(message = "Owner name must not be null")
    @ValidName
    private String ownerName;

    @NotNull(message = "Account type must not be null")
    @ValidAccountType
    private AccountType accountType;

    @NotNull(message = "City must not be null")
    @ValidBranch
    private Branch branch;

}