package com.jayaram.spendwise_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.jayaram.spendwise_service.dto.ExpenseDetailCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseDetailUpdateRequest;
import com.jayaram.spendwise_service.service.ExpenseDetailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/expense-details")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class ExpenseDetailController {

    private final ExpenseDetailService expenseDetailService;

    @PostMapping
    public ResponseEntity<ExpenseDetailResponse> createExpenseDetail(
            @Valid @RequestBody ExpenseDetailCreateRequest request) {
        log.info("Create expense detail request received");
        ExpenseDetailResponse created = expenseDetailService.createExpenseDetail(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDetailResponse>> getAllExpenseDetails() {
        log.info("Get all expense details request received");
        return ResponseEntity.ok(expenseDetailService.getAllExpenseDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDetailResponse> getExpenseDetailById(@PathVariable Long id) {
        log.info("Get expense detail by id request received id={}", id);
        return ResponseEntity.ok(expenseDetailService.getExpenseDetailById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDetailResponse> updateExpenseDetail(@PathVariable Long id,
            @Valid @RequestBody ExpenseDetailUpdateRequest request) {
        log.info("Update expense detail request received id={}", id);
        return ResponseEntity.ok(expenseDetailService.updateExpenseDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseDetail(@PathVariable Long id) {
        log.info("Delete expense detail request received id={}", id);
        expenseDetailService.deleteExpenseDetail(id);
        return ResponseEntity.noContent().build();
    }
}
