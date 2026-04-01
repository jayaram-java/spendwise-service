package com.jayaram.spendwise_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseDetailCreateRequest {

    @NotBlank(message = "is required")
    private String expenseName;

    @NotNull(message = "is required")
    private LocalDate expenseDate;

    @NotNull(message = "is required")
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

    @NotNull(message = "is required")
    private Long categoryId;

    private String createdBy;
}
