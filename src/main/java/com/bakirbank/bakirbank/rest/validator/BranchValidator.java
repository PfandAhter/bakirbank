package com.bakirbank.bakirbank.rest.validator;

import com.bakirbank.bakirbank.exception.NotFoundException;
import com.bakirbank.bakirbank.model.Branch;
import com.bakirbank.bakirbank.repository.BranchRepository;
import com.bakirbank.bakirbank.rest.validator.annotations.ValidBranch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.bakirbank.bakirbank.lib.ErrorCodeConstants.BRANCH_NOT_FOUND;

import java.util.Optional;


@RequiredArgsConstructor
@Slf4j

public class BranchValidator implements ConstraintValidator<ValidBranch,String> {

    private final BranchRepository branchRepository;

    @Override
    public void initialize(ValidBranch constraintAnnotation){
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String branchId, ConstraintValidatorContext constraintValidatorContext){
        try {
            Optional<Branch> optionalBranch = branchRepository.findBranchByBranchId(branchId);
            optionalBranch.orElseThrow(() -> new NotFoundException(BRANCH_NOT_FOUND));

            /*optionalBranch.ifPresent(branch -> {
                if (branch.getBranchId().equals(branchId)) {
                    log.info("Branch is valid");
                    return true;
                }
            });*/

            return true;
        }catch (NotFoundException exception){
            log.error("Branch not found", exception);
            return false;
        }
    }

}
