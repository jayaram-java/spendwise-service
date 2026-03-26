package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseDailySpendItem {

    private LocalDate date;
    private String dayLabel;
    private BigDecimal amount;
}
