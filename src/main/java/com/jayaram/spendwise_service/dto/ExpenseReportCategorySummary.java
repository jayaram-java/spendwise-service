package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseReportCategorySummary {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private Long totalCount;
}
