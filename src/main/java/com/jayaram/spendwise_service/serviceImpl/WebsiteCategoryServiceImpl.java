package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.WebsiteCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteCategoryResponse;
import com.jayaram.spendwise_service.dto.WebsiteCategoryUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.WebsiteCategory;
import com.jayaram.spendwise_service.repository.WebsiteCategoryRepository;
import com.jayaram.spendwise_service.service.WebsiteCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebsiteCategoryServiceImpl implements WebsiteCategoryService {

    private final WebsiteCategoryRepository websiteCategoryRepository;

    @Override
    public WebsiteCategoryResponse createCategory(WebsiteCategoryCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Website category payload is required");
        }

        websiteCategoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName())
                .ifPresent(existing -> {
                    throw new BadRequestException("Website category already exists");
                });

        WebsiteCategory category = WebsiteCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();

        String createdBy = request.getCreatedBy();
        if (createdBy == null || createdBy.trim().isEmpty()) {
            createdBy = "system";
        }
        category.setCreatedBy(createdBy);
        category.setCreatedDate(LocalDateTime.now());

        WebsiteCategory saved = websiteCategoryRepository.save(category);
        log.info("Created website category id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<WebsiteCategoryResponse> getAllCategories() {
        List<WebsiteCategory> categories = websiteCategoryRepository.findAll();
        log.info("Fetched {} website categories", categories.size());
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public WebsiteCategoryResponse getCategoryById(Long id) {
        WebsiteCategory category = websiteCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website category not found: " + id));
        log.info("Fetched website category id={}", id);
        return toResponse(category);
    }

    @Override
    public WebsiteCategoryResponse updateCategory(Long id, WebsiteCategoryUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Website category payload is required");
        }

        WebsiteCategory existing = websiteCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website category not found: " + id));

        if (request.getCategoryName() != null) {
            websiteCategoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName())
                    .filter(found -> !found.getId().equals(existing.getId()))
                    .ifPresent(found -> {
                        throw new BadRequestException("Website category already exists");
                    });
            existing.setCategoryName(request.getCategoryName());
        }

        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        WebsiteCategory saved = websiteCategoryRepository.save(existing);
        log.info("Updated website category id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        WebsiteCategory existing = websiteCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website category not found: " + id));
        websiteCategoryRepository.delete(existing);
        log.info("Deleted website category id={}", id);
    }

    private WebsiteCategoryResponse toResponse(WebsiteCategory category) {
        return WebsiteCategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .createdBy(category.getCreatedBy())
                .createdDate(category.getCreatedDate())
                .modifiedBy(category.getModifiedBy())
                .modifiedDate(category.getModifiedDate())
                .build();
    }
}
