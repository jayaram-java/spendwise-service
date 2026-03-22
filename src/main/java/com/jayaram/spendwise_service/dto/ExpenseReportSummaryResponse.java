package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseReportSummaryResponse {

    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currency;
    private BigDecimal totalAmount;
    private Long totalCount;
    private List<ExpenseReportCategorySummary> categorySummaries;
}
