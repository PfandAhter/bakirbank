package com.bakirbank.bakirbank.repository;

import com.bakirbank.bakirbank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {


}
