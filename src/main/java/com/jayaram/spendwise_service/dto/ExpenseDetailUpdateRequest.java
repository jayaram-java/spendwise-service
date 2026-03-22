package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseDetailUpdateRequest {

    private String expenseName;

    private LocalDate expenseDate;

    @DecimalMin(value = "0.01", message = "must be greater than zero")
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

    private String modifiedBy;
}
