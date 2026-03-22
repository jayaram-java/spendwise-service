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

import com.jayaram.spendwise_service.dto.ExpenseCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseCategoryResponse;
import com.jayaram.spendwise_service.dto.ExpenseCategoryUpdateRequest;
import com.jayaram.spendwise_service.service.ExpenseCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/expense-categories")
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @PostMapping
    public ResponseEntity<ExpenseCategoryResponse> createCategory(
            @Valid @RequestBody ExpenseCategoryCreateRequest request) {
        log.info("Create expense category request received");
        ExpenseCategoryResponse created = expenseCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseCategoryResponse>> getAllCategories() {
        log.info("Get all expense categories request received");
        return ResponseEntity.ok(expenseCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Get expense category by id request received id={}", id);
        return ResponseEntity.ok(expenseCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategoryResponse> updateCategory(@PathVariable Long id,
            @Valid @RequestBody ExpenseCategoryUpdateRequest request) {
        log.info("Update expense category request received id={}", id);
        return ResponseEntity.ok(expenseCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Delete expense category request received id={}", id);
        expenseCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
