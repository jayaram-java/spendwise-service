package com.jayaram.spendwise_service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.model.ExpenseDetail;
import com.jayaram.spendwise_service.repository.ExpenseDetailRepository;

@Component
public class ExpenseCodeGenerator {

    private static final String PREFIX = "EXP";
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    private final ExpenseDetailRepository expenseDetailRepository;

    public ExpenseCodeGenerator(ExpenseDetailRepository expenseDetailRepository) {
        this.expenseDetailRepository = expenseDetailRepository;
    }

    public String generateNextExpenseCode(LocalDate expenseDate) {
        Long userId = getCurrentUserId();
        return generateNextExpenseCode(userId, expenseDate);
    }

    public String generateNextExpenseCode(Long userId, LocalDate expenseDate) {
        if (userId == null) {
            throw new BadRequestException("User id is required to generate expense code");
        }
        LocalDate effectiveDate = expenseDate == null ? LocalDate.now() : expenseDate;
        String yearMonth = effectiveDate.format(YEAR_MONTH);
        String prefix = PREFIX + "-" + userId + "-" + yearMonth + "-";

        int nextSequence = expenseDetailRepository
                .findTopByUserIdAndExpenseCodeStartingWithOrderByIdDesc(userId, prefix)
                .map(ExpenseDetail::getExpenseCode)
                .map(code -> parseSequence(code, prefix))
                .map(value -> value + 1)
                .orElse(1);

        return prefix + String.format("%04d", nextSequence);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User id is required but missing from security context");
        }

        String rawId = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Number number) {
            return number.longValue();
        }
        if (principal instanceof String principalString) {
            rawId = principalString;
        }
        if (rawId == null || rawId.isBlank()) {
            rawId = authentication.getName();
        }

        try {
            return Long.parseLong(rawId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("User id in security context is not a valid number");
        }
    }

    private int parseSequence(String expenseCode, String prefix) {
        if (expenseCode == null || !expenseCode.startsWith(prefix)) {
            return 0;
        }
        String suffix = expenseCode.substring(prefix.length());
        if (suffix.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
