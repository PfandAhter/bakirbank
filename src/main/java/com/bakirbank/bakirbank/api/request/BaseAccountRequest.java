package com.bakirbank.bakirbank.api.request;

import com.bakirbank.bakirbank.model.enums.Currency;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidCurrency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseAccountRequest extends BaseRequest{

    @NotBlank(message = "Customer id must not be null")
    private String customerId;

    @NotNull
    @Min(0)
    private Double balance;

    @NotNull(message = "Currency must not be null")
    @ValidCurrency
    private Currency currency;

}