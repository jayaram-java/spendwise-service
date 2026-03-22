package com.jayaram.spendwise_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.ExpenseCategory;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    List<ExpenseCategory> findByIsDeletedFalse();

    List<ExpenseCategory> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<ExpenseCategory> findByNameAndUserId(String name, Long userId);
}
