package com.bakirbank.bakirbank.rest.validator;

import com.bakirbank.bakirbank.rest.validator.annotations.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NameValidator implements ConstraintValidator<ValidName,String> {

    private static final String NAME_PATTERN = "^[a-zA-Z]*$"; //sadece harf olacak şekilde regex

    @Override
    public void initialize(ValidName constraintAnnotation){
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid (String name, ConstraintValidatorContext constraintValidatorContext){
        if(name == null || name.isBlank()){
            return false;
        }

        return name.matches(NAME_PATTERN);
    }
}
