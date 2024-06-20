package com.zulkan.ewallet.repository;

import com.zulkan.ewallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "SELECT * FROM transactions t WHERE source_id = :userId OR destination_id = :userId ORDER BY amount DESC LIMIT 10", nativeQuery = true)
    List<Transaction> getTopTransactions(@Param("userId") Integer userId);

}
