package com.bakirbank.bakirbank.rest.validator.annotations;


import com.bakirbank.bakirbank.rest.validator.BranchValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BranchValidator.class})
public @interface ValidBranch {
    String message() default "Invalid branch: No branch found with this code";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}