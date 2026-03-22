package com.jayaram.spendwise_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.ExpenseDetail;

public interface ExpenseDetailRepository extends JpaRepository<ExpenseDetail, Long> {

    List<ExpenseDetail> findByIsDeletedFalse();

    List<ExpenseDetail> findByUserIdAndIsDeletedFalse(Long userId);

    List<ExpenseDetail> findByUserIdAndExpenseDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);

    List<ExpenseDetail> findByCategoryId(Long categoryId);

    List<ExpenseDetail> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<ExpenseDetail> findByUserIdAndExpenseDateBetweenAndIsDeletedFalse(
            Long userId, LocalDate startDate, LocalDate endDate);

    List<ExpenseDetail> findByUserIdAndCategoryIdAndIsDeletedFalse(Long userId, Long categoryId);

    List<ExpenseDetail> findByUserIdAndCategoryIdAndExpenseDateBetweenAndIsDeletedFalse(
            Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
}
