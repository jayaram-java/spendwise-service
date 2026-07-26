package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryResponse {

    private BigDecimal totalExpense;
    private Long totalTransactions;
    private BigDecimal averageExpense;
    private BigDecimal highestExpense;
    private BigDecimal lowestExpense;
}
