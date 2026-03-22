package com.jayaram.spendwise_service.service;

import java.time.LocalDate;
import java.util.List;

import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseReportSummaryResponse;

public interface ExpenseReportService {

    ExpenseReportSummaryResponse getExpenseSummary(Long userId, LocalDate startDate, LocalDate endDate,
            Long categoryId);

    List<ExpenseDetailResponse> getExpenseDetails(Long userId, LocalDate startDate, LocalDate endDate,
            Long categoryId);
}
