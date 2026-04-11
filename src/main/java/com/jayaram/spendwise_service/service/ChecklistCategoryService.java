package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.ChecklistCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistCategoryResponse;
import com.jayaram.spendwise_service.dto.ChecklistCategoryUpdateRequest;

public interface ChecklistCategoryService {

    ChecklistCategoryResponse createCategory(ChecklistCategoryCreateRequest request);

    List<ChecklistCategoryResponse> getAllCategories();

    ChecklistCategoryResponse getCategoryById(Long id);

    ChecklistCategoryResponse updateCategory(Long id, ChecklistCategoryUpdateRequest request);

    void deleteCategory(Long id);
}
