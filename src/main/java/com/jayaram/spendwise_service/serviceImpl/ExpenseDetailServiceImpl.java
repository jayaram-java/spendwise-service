package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.ExpenseDetailCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseDetailUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.ExpenseCategory;
import com.jayaram.spendwise_service.model.ExpenseDetail;
import com.jayaram.spendwise_service.repository.ExpenseCategoryRepository;
import com.jayaram.spendwise_service.repository.ExpenseDetailRepository;
import com.jayaram.spendwise_service.service.ExpenseDetailService;
import com.jayaram.spendwise_service.util.ExpenseCodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseDetailServiceImpl implements ExpenseDetailService {

    private final ExpenseDetailRepository expenseDetailRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCodeGenerator expenseCodeGenerator;

    @Override
    public ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Expense detail payload is required");
        }

        ExpenseCategory category = resolveCategory(request.getCategoryId());
        Long userId = expenseCodeGenerator.getCurrentUserId();
        String expenseCode = expenseCodeGenerator.generateNextExpenseCode(userId, request.getExpenseDate());

        ExpenseDetail detail = ExpenseDetail.builder()
                .expenseName(request.getExpenseName())
                .expenseDate(request.getExpenseDate())
                .amount(request.getAmount())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .expenseCode(expenseCode)
                .referenceNumber(request.getReferenceNumber())
                .receiptUrl(request.getReceiptUrl())
                .currency(request.getCurrency() == null ? "INR" : request.getCurrency())
                .userId(userId)
                .status(request.getStatus() == null ? "ACTIVE" : request.getStatus())
                .isDeleted(false)
                .category(category)
                .build();

        if (request.getCreatedBy() == null || request.getCreatedBy().trim().isEmpty()) {
            detail.setCreatedBy("system");
        } else {
            detail.setCreatedBy(request.getCreatedBy());
        }
        detail.setCreatedDate(LocalDateTime.now());

        ExpenseDetail saved = expenseDetailRepository.save(detail);
        log.info("Created expense detail id={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    @Override
    public List<ExpenseDetailResponse> getAllExpenseDetails() {
        List<ExpenseDetail> details = expenseDetailRepository.findByIsDeletedFalseOrderByExpenseDateDesc();
        log.info("Fetched {} expense details", details.size());
        return details.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ExpenseDetailResponse getExpenseDetailById(Long id) {
        ExpenseDetail detail = expenseDetailRepository.findById(id)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense detail not found: " + id));
        log.info("Fetched expense detail id={}", id);
        return toResponse(detail);
    }

    @Override
    public ExpenseDetailResponse updateExpenseDetail(Long id, ExpenseDetailUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Expense detail payload is required");
        }

        ExpenseDetail existing = expenseDetailRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense detail not found: " + id));

        if (request.getExpenseName() != null) {
            existing.setExpenseName(request.getExpenseName());
        }
        if (request.getExpenseDate() != null) {
            existing.setExpenseDate(request.getExpenseDate());
        }
        if (request.getAmount() != null) {
            existing.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getPaymentMethod() != null) {
            existing.setPaymentMethod(request.getPaymentMethod());
        }
        if (request.getExpenseCode() != null) {
            existing.setExpenseCode(request.getExpenseCode());
        }
        if (request.getReferenceNumber() != null) {
            existing.setReferenceNumber(request.getReferenceNumber());
        }
        if (request.getReceiptUrl() != null) {
            existing.setReceiptUrl(request.getReceiptUrl());
        }
        if (request.getCurrency() != null) {
            existing.setCurrency(request.getCurrency());
        }
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }

        if (request.getCategoryId() != null) {
            ExpenseCategory category = resolveCategory(request.getCategoryId());
            existing.setCategory(category);
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        ExpenseDetail saved = expenseDetailRepository.save(existing);
        log.info("Updated expense detail id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteExpenseDetail(Long id) {
        ExpenseDetail existing = expenseDetailRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense detail not found: " + id));

        existing.setIsDeleted(true);
        existing.setModifiedBy("system");
        existing.setModifiedDate(LocalDateTime.now());
        expenseDetailRepository.save(existing);
        log.info("Soft deleted expense detail id={}", id);
    }

    private ExpenseCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BadRequestException("Category id is required");
        }

        return expenseCategoryRepository.findById(categoryId)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + categoryId));
    }

    private ExpenseDetailResponse toResponse(ExpenseDetail detail) {
        return ExpenseDetailResponse.builder()
                .id(detail.getId())
                .expenseName(detail.getExpenseName())
                .expenseDate(detail.getExpenseDate())
                .amount(detail.getAmount())
                .description(detail.getDescription())
                .paymentMethod(detail.getPaymentMethod())
                .expenseCode(detail.getExpenseCode())
                .referenceNumber(detail.getReferenceNumber())
                .receiptUrl(detail.getReceiptUrl())
                .currency(detail.getCurrency())
                .userId(detail.getUserId())
                .status(detail.getStatus())
                .categoryId(detail.getCategory() != null ? detail.getCategory().getId() : null)
                .createdBy(detail.getCreatedBy())
                .createdDate(detail.getCreatedDate())
                .modifiedBy(detail.getModifiedBy())
                .modifiedDate(detail.getModifiedDate())
                .build();
    }
}
