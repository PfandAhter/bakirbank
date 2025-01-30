package com.bakirbank.bakirbank.repository;

import com.bakirbank.bakirbank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {

    @Query("SELECT a FROM Account a WHERE a.accountNumber = ?1")
    Optional<Account> findAccountByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.customerId = ?1 AND a.status = 'ACTIVE' AND a.name = ?2")
    Optional<Account> findAccountByCustomerIdAndAccountName(String customerId, String accountName);

}