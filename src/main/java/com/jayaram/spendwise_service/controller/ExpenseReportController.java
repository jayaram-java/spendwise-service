package com.jayaram.spendwise_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseReportSummaryResponse;
import com.jayaram.spendwise_service.service.ExpenseReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/expense-reports")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class ExpenseReportController {

    private final ExpenseReportService expenseReportService;

    @GetMapping("/summary")
    public ResponseEntity<ExpenseReportSummaryResponse> getExpenseSummary(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {
        log.info("Expense summary request received userId={} categoryId={}", userId, categoryId);
        ExpenseReportSummaryResponse summary = expenseReportService.getExpenseSummary(userId, startDate, endDate,
                categoryId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/details")
    public ResponseEntity<List<ExpenseDetailResponse>> getExpenseDetails(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {
        log.info("Expense details report request received userId={} categoryId={}", userId, categoryId);
        List<ExpenseDetailResponse> details = expenseReportService.getExpenseDetails(userId, startDate, endDate,
                categoryId);
        return ResponseEntity.ok(details);
    }
}
