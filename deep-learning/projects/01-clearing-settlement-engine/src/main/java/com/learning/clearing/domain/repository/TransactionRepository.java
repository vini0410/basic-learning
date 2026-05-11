package com.learning.clearing.domain.repository;

import com.learning.clearing.domain.model.Transaction;
import com.learning.clearing.domain.model.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {
    List<Transaction> findBySourceAccountNumberOrDestinationAccountNumber(String source, String destination);
}
