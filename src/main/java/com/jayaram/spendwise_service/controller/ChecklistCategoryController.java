package com.jayaram.spendwise_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayaram.spendwise_service.dto.ChecklistCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistCategoryResponse;
import com.jayaram.spendwise_service.dto.ChecklistCategoryUpdateRequest;
import com.jayaram.spendwise_service.service.ChecklistCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/checklist-categories")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class ChecklistCategoryController {

    private final ChecklistCategoryService checklistCategoryService;

    @PostMapping
    public ResponseEntity<ChecklistCategoryResponse> createCategory(
            @Valid @RequestBody ChecklistCategoryCreateRequest request) {
        log.info("Create checklist category request received");
        ChecklistCategoryResponse created = checklistCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ChecklistCategoryResponse>> getAllCategories() {
        log.info("Get all checklist categories request received");
        return ResponseEntity.ok(checklistCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistCategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Get checklist category by id request received id={}", id);
        return ResponseEntity.ok(checklistCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChecklistCategoryResponse> updateCategory(@PathVariable Long id,
            @Valid @RequestBody ChecklistCategoryUpdateRequest request) {
        log.info("Update checklist category request received id={}", id);
        return ResponseEntity.ok(checklistCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Delete checklist category request received id={}", id);
        checklistCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
