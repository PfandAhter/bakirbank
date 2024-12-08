package com.bakirbank.bakirbank.rest.validator;

import com.bakirbank.bakirbank.model.enums.AccountType;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidAccountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountTypeValidator implements ConstraintValidator<ValidAccountType, String> {

    @Override
    public void initialize (ValidAccountType constraintAnnotation){
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid (String value, ConstraintValidatorContext constraintValidatorContext){
        if (value == null){
            return false;
        }
        AccountType[] accountTypes = AccountType.values();

        for (AccountType accountType : accountTypes){
            if (accountType.name().equals(value)){
                return true;
            }
        }
        return false;
    }
}