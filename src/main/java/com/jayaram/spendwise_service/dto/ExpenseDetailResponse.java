package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseDetailResponse {

    private Long id;
    private String expenseName;
    private LocalDate expenseDate;
    private BigDecimal amount;
    private String description;
    private String paymentMethod;
    private String expenseCode;
    private String referenceNumber;
    private String receiptUrl;
    private String currency;
    private Long userId;
    private String status;
    private Long categoryId;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
