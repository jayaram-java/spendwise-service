package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseMonthlySpendItem {

    private LocalDate monthStart;
    private String monthLabel;
    private BigDecimal amount;
}
