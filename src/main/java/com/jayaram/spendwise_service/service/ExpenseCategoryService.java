package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.ExpenseCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseCategoryResponse;
import com.jayaram.spendwise_service.dto.ExpenseCategoryUpdateRequest;

public interface ExpenseCategoryService {

    ExpenseCategoryResponse createCategory(ExpenseCategoryCreateRequest request);

    List<ExpenseCategoryResponse> getAllCategories();

    ExpenseCategoryResponse getCategoryById(Long id);

    ExpenseCategoryResponse updateCategory(Long id, ExpenseCategoryUpdateRequest request);

    void deleteCategory(Long id);
}
