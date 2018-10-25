package com.future.office_inventory_system.service;

import com.future.office_inventory_system.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    Integer MAX_ALLOWABLE_MILISECONDS_TO_UPDATE = 3600;

    Transaction createTransaction(Transaction transaction);
    Page<Transaction> readAllTransactions(Transaction transaction, Pageable pageable);
    Transaction readTransactionByIdTransaction(Long id);
    ResponseEntity deleteTransaction(Long id);
    
}