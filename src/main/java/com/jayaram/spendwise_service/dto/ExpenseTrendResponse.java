package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseTrendResponse {

    private Long userId;
    private String currency;

    private LocalDate dailyStartDate;
    private LocalDate dailyEndDate;
    private BigDecimal dailyTotalAmount;
    private List<ExpenseDailySpendItem> dailySpends;

    private LocalDate monthlyStartDate;
    private LocalDate monthlyEndDate;
    private BigDecimal monthlyTotalAmount;
    private List<ExpenseMonthlySpendItem> monthlySpends;
}
