package com.bakirbank.bakirbank.repository;

import com.bakirbank.bakirbank.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,String> {

    @Query("SELECT b FROM Branch b WHERE b.branchId = ?1")
    Optional<Branch> findBranchByBranchId(String branchId);

}