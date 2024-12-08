package com.bakirbank.bakirbank.rest.validator.annotations;


import com.bakirbank.bakirbank.rest.validator.CurrencyValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CurrencyValidator.class})
public @interface ValidCurrency {

    String message() default "Invalid currency: Currency must be in USD, EUR, TRY, GBP, JPY format";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
