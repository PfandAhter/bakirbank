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

}