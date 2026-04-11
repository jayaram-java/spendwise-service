package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.ChecklistCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistCategoryResponse;
import com.jayaram.spendwise_service.dto.ChecklistCategoryUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.ChecklistCategory;
import com.jayaram.spendwise_service.repository.ChecklistCategoryRepository;
import com.jayaram.spendwise_service.service.ChecklistCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistCategoryServiceImpl implements ChecklistCategoryService {

    private final ChecklistCategoryRepository checklistCategoryRepository;

    @Override
    public ChecklistCategoryResponse createCategory(ChecklistCategoryCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Checklist category payload is required");
        }

        checklistCategoryRepository.findByNameAndUserId(request.getName(), request.getUserId())
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .ifPresent(existing -> {
                    throw new BadRequestException("Checklist category already exists for this user");
                });

        ChecklistCategory category = new ChecklistCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());
        category.setIsPrimary(request.getIsPrimary() == null ? Boolean.FALSE : request.getIsPrimary());
        category.setUserId(request.getUserId());
        category.setIsDeleted(false);

        String createdBy = request.getCreatedBy();
        if (createdBy == null || createdBy.trim().isEmpty()) {
            createdBy = "system";
        }
        category.setCreatedBy(createdBy);
        category.setCreatedDate(LocalDateTime.now());

        ChecklistCategory saved = checklistCategoryRepository.save(category);
        log.info("Created checklist category id={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    @Override
    public List<ChecklistCategoryResponse> getAllCategories() {
        List<ChecklistCategory> categories = checklistCategoryRepository.findByIsDeletedFalse();
        log.info("Fetched {} checklist categories", categories.size());
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ChecklistCategoryResponse getCategoryById(Long id) {
        ChecklistCategory category = checklistCategoryRepository.findById(id)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist category not found: " + id));
        log.info("Fetched checklist category id={}", id);
        return toResponse(category);
    }

    @Override
    public ChecklistCategoryResponse updateCategory(Long id, ChecklistCategoryUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Checklist category payload is required");
        }

        ChecklistCategory existing = checklistCategoryRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist category not found: " + id));

        String newName = request.getName() != null ? request.getName() : existing.getName();
        Long newUserId = request.getUserId() != null ? request.getUserId() : existing.getUserId();

        checklistCategoryRepository.findByNameAndUserId(newName, newUserId)
                .filter(found -> !found.getId().equals(existing.getId()))
                .ifPresent(found -> {
                    throw new BadRequestException("Checklist category already exists for this user");
                });

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        if (request.getIsPrimary() != null) {
            existing.setIsPrimary(request.getIsPrimary());
        }
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        ChecklistCategory saved = checklistCategoryRepository.save(existing);
        log.info("Updated checklist category id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        ChecklistCategory existing = checklistCategoryRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist category not found: " + id));

        existing.setIsDeleted(true);
        existing.setModifiedBy("system");
        existing.setModifiedDate(LocalDateTime.now());
        checklistCategoryRepository.save(existing);
        log.info("Soft deleted checklist category id={}", id);
    }

    private ChecklistCategoryResponse toResponse(ChecklistCategory category) {
        return ChecklistCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .isPrimary(category.getIsPrimary())
                .userId(category.getUserId())
                .createdBy(category.getCreatedBy())
                .createdDate(category.getCreatedDate())
                .modifiedBy(category.getModifiedBy())
                .modifiedDate(category.getModifiedDate())
                .build();
    }
}
