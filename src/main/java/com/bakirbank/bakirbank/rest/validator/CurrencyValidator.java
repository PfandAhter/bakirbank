package com.bakirbank.bakirbank.rest.validator;

import com.bakirbank.bakirbank.model.enums.Currency;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidCurrency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CurrencyValidator implements ConstraintValidator<ValidCurrency,String> {

    @Override
    public void initialize (ValidCurrency constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid (String value, ConstraintValidatorContext constraintValidatorContext){
        if (value == null){
            return false;
        }
        Currency[] currencies = Currency.values();
        for (Currency currency : currencies){
            if (currency.name().equals(value)){
                return true;
            }
        }

        return false;
    }
}
