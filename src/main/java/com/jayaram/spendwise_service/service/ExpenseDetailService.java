package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.ExpenseDetailCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseDetailUpdateRequest;

public interface ExpenseDetailService {

    ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request);

    List<ExpenseDetailResponse> getAllExpenseDetails();

    ExpenseDetailResponse getExpenseDetailById(Long id);

    ExpenseDetailResponse updateExpenseDetail(Long id, ExpenseDetailUpdateRequest request);

    void deleteExpenseDetail(Long id);
}
