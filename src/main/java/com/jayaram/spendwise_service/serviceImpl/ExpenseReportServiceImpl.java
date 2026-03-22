package com.jayaram.spendwise_service.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseReportCategorySummary;
import com.jayaram.spendwise_service.dto.ExpenseReportSummaryResponse;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.ExpenseCategory;
import com.jayaram.spendwise_service.model.ExpenseDetail;
import com.jayaram.spendwise_service.repository.ExpenseCategoryRepository;
import com.jayaram.spendwise_service.repository.ExpenseDetailRepository;
import com.jayaram.spendwise_service.service.ExpenseReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseReportServiceImpl implements ExpenseReportService {

    private final ExpenseDetailRepository expenseDetailRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public ExpenseReportSummaryResponse getExpenseSummary(Long userId, LocalDate startDate, LocalDate endDate,
            Long categoryId) {
        validateUserId(userId);
        validateDateRange(startDate, endDate);
        ExpenseCategory category = resolveCategoryIfPresent(userId, categoryId);

        List<ExpenseDetail> details = fetchDetails(userId, startDate, endDate, categoryId);
        log.info("Fetched {} expense records for summary userId={} categoryId={}", details.size(), userId, categoryId);

        Map<Long, ExpenseCategory> categoryMap = loadCategories(details, category);
        List<ExpenseReportCategorySummary> categorySummaries = buildCategorySummaries(details, categoryMap);

        BigDecimal totalAmount = details.stream()
                .map(ExpenseDetail::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = details.stream()
                .map(ExpenseDetail::getCurrency)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("INR");

        return ExpenseReportSummaryResponse.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .currency(currency)
                .totalAmount(totalAmount)
                .totalCount((long) details.size())
                .categorySummaries(categorySummaries)
                .build();
    }

    @Override
    public List<ExpenseDetailResponse> getExpenseDetails(Long userId, LocalDate startDate, LocalDate endDate,
            Long categoryId) {
        validateUserId(userId);
        validateDateRange(startDate, endDate);
        resolveCategoryIfPresent(userId, categoryId);

        List<ExpenseDetail> details = fetchDetails(userId, startDate, endDate, categoryId);
        log.info("Fetched {} expense records for details userId={} categoryId={}", details.size(), userId, categoryId);
        return details.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BadRequestException("userId is required");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if ((startDate == null) != (endDate == null)) {
            throw new BadRequestException("Both startDate and endDate are required together");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be on or before endDate");
        }
    }

    private ExpenseCategory resolveCategoryIfPresent(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + categoryId));

        if (!userId.equals(category.getUserId())) {
            throw new BadRequestException("Category does not belong to the user");
        }
        return category;
    }

    private List<ExpenseDetail> fetchDetails(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId) {
        if (categoryId != null) {
            if (startDate != null) {
                return expenseDetailRepository.findByUserIdAndCategoryIdAndExpenseDateBetweenAndIsDeletedFalse(
                        userId, categoryId, startDate, endDate);
            }
            return expenseDetailRepository.findByUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId);
        }

        if (startDate != null) {
            return expenseDetailRepository.findByUserIdAndExpenseDateBetweenAndIsDeletedFalse(
                    userId, startDate, endDate);
        }
        return expenseDetailRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    private Map<Long, ExpenseCategory> loadCategories(List<ExpenseDetail> details, ExpenseCategory selectedCategory) {
        Map<Long, ExpenseCategory> categoryMap = new HashMap<>();
        if (selectedCategory != null) {
            categoryMap.put(selectedCategory.getId(), selectedCategory);
            return categoryMap;
        }

        List<Long> categoryIds = details.stream()
                .map(detail -> detail.getCategory() != null ? detail.getCategory().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            return categoryMap;
        }

        expenseCategoryRepository.findAllById(categoryIds).forEach(category -> {
            if (Boolean.FALSE.equals(category.getIsDeleted())) {
                categoryMap.put(category.getId(), category);
            }
        });
        return categoryMap;
    }

    private List<ExpenseReportCategorySummary> buildCategorySummaries(List<ExpenseDetail> details,
            Map<Long, ExpenseCategory> categoryMap) {
        Map<Long, CategoryAccumulator> accumulators = new HashMap<>();

        for (ExpenseDetail detail : details) {
            Long categoryId = detail.getCategory() != null ? detail.getCategory().getId() : null;
            if (categoryId == null) {
                continue;
            }

            CategoryAccumulator accumulator = accumulators.computeIfAbsent(categoryId, key -> new CategoryAccumulator());
            accumulator.totalAmount = accumulator.totalAmount.add(
                    detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
            accumulator.totalCount += 1;
        }

        List<ExpenseReportCategorySummary> summaries = new ArrayList<>();
        for (Map.Entry<Long, CategoryAccumulator> entry : accumulators.entrySet()) {
            ExpenseCategory category = categoryMap.get(entry.getKey());
            String categoryName = category != null ? category.getName() : null;
            CategoryAccumulator accumulator = entry.getValue();
            summaries.add(ExpenseReportCategorySummary.builder()
                    .categoryId(entry.getKey())
                    .categoryName(categoryName)
                    .totalAmount(accumulator.totalAmount)
                    .totalCount(accumulator.totalCount)
                    .build());
        }

        summaries.sort(Comparator.comparing(ExpenseReportCategorySummary::getTotalAmount).reversed());
        return summaries;
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

    private static class CategoryAccumulator {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private long totalCount = 0;
    }
}
