package com.bakirbank.bakirbank.rest.validator.annotations;


import com.bakirbank.bakirbank.rest.validator.AccountTypeValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AccountTypeValidator.class})
public @interface ValidAccountType {
    String message() default "Invalid account type: Account type must be either 'SAVINGS' or 'CURRENT'";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
