package com.bakirbank.bakirbank.repository;

import com.bakirbank.bakirbank.model.ErrorCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCodes,String> {

}
