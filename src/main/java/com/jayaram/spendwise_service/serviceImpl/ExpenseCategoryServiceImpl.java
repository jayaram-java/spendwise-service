package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.ExpenseCategoryCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseCategoryResponse;
import com.jayaram.spendwise_service.dto.ExpenseCategoryUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.ExpenseCategory;
import com.jayaram.spendwise_service.repository.ExpenseCategoryRepository;
import com.jayaram.spendwise_service.service.ExpenseCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public ExpenseCategoryResponse createCategory(ExpenseCategoryCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Expense category payload is required");
        }

        expenseCategoryRepository.findByNameAndUserId(request.getName(), request.getUserId())
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .ifPresent(existing -> {
                    throw new BadRequestException("Category already exists for this user");
                });

        ExpenseCategory category = ExpenseCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .userId(request.getUserId())
                .permissionId(request.getPermissionId())
                .status(request.getStatus() == null ? "ACTIVE" : request.getStatus())
                .isDeleted(false)
                .build();

        if (request.getCreatedBy() == null || request.getCreatedBy().trim().isEmpty()) {
            category.setCreatedBy("system");
        } else {
            category.setCreatedBy(request.getCreatedBy());
        }
        category.setCreatedDate(LocalDateTime.now());

        ExpenseCategory saved = expenseCategoryRepository.save(category);
        log.info("Created expense category id={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    @Override
    public List<ExpenseCategoryResponse> getAllCategories() {
        List<ExpenseCategory> categories = expenseCategoryRepository.findByIsDeletedFalse();
        log.info("Fetched {} expense categories", categories.size());
        return categories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ExpenseCategoryResponse getCategoryById(Long id) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + id));
        log.info("Fetched expense category id={}", id);
        return toResponse(category);
    }

    @Override
    public ExpenseCategoryResponse updateCategory(Long id, ExpenseCategoryUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Expense category payload is required");
        }

        ExpenseCategory existing = expenseCategoryRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + id));

        String newName = request.getName() != null ? request.getName() : existing.getName();
        Long newUserId = request.getUserId() != null ? request.getUserId() : existing.getUserId();

        expenseCategoryRepository.findByNameAndUserId(newName, newUserId)
                .filter(found -> !found.getId().equals(existing.getId()))
                .ifPresent(found -> {
                    throw new BadRequestException("Category already exists for this user");
                });

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getPermissionId() != null) {
            existing.setPermissionId(request.getPermissionId());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        ExpenseCategory saved = expenseCategoryRepository.save(existing);
        log.info("Updated expense category id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        ExpenseCategory existing = expenseCategoryRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + id));

        existing.setIsDeleted(true);
        existing.setModifiedBy("system");
        existing.setModifiedDate(LocalDateTime.now());
        expenseCategoryRepository.save(existing);
        log.info("Soft deleted expense category id={}", id);
    }

    private ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        return ExpenseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .userId(category.getUserId())
                .permissionId(category.getPermissionId())
                .status(category.getStatus())
                .createdBy(category.getCreatedBy())
                .createdDate(category.getCreatedDate())
                .modifiedBy(category.getModifiedBy())
                .modifiedDate(category.getModifiedDate())
                .build();
    }
}
