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

import com.jayaram.spendwise_service.dto.WebsiteCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteCategoryResponse;
import com.jayaram.spendwise_service.dto.WebsiteCategoryUpdateRequest;
import com.jayaram.spendwise_service.service.WebsiteCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/website-categories")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class WebsiteCategoryController {

    private final WebsiteCategoryService websiteCategoryService;

    @PostMapping
    public ResponseEntity<WebsiteCategoryResponse> createCategory(
            @Valid @RequestBody WebsiteCategoryCreateRequest request) {
        log.info("Create website category request received");
        WebsiteCategoryResponse created = websiteCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<WebsiteCategoryResponse>> getAllCategories() {
        log.info("Get all website categories request received");
        return ResponseEntity.ok(websiteCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebsiteCategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Get website category by id request received id={}", id);
        return ResponseEntity.ok(websiteCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebsiteCategoryResponse> updateCategory(@PathVariable Long id,
            @Valid @RequestBody WebsiteCategoryUpdateRequest request) {
        log.info("Update website category request received id={}", id);
        return ResponseEntity.ok(websiteCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Delete website category request received id={}", id);
        websiteCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
