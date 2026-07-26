package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.WebsiteCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteCategoryResponse;
import com.jayaram.spendwise_service.dto.WebsiteCategoryUpdateRequest;

public interface WebsiteCategoryService {

    WebsiteCategoryResponse createCategory(WebsiteCategoryCreateRequest request);

    List<WebsiteCategoryResponse> getAllCategories();

    WebsiteCategoryResponse getCategoryById(Long id);

    WebsiteCategoryResponse updateCategory(Long id, WebsiteCategoryUpdateRequest request);

    void deleteCategory(Long id);
}
