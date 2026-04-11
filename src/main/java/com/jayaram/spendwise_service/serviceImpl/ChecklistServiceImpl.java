package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.ChecklistCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistResponse;
import com.jayaram.spendwise_service.dto.ChecklistUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.Checklist;
import com.jayaram.spendwise_service.model.ChecklistCategory;
import com.jayaram.spendwise_service.repository.ChecklistCategoryRepository;
import com.jayaram.spendwise_service.repository.ChecklistRepository;
import com.jayaram.spendwise_service.service.ChecklistService;
import com.jayaram.spendwise_service.util.ChecklistStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistCategoryRepository checklistCategoryRepository;

    @Override
    public ChecklistResponse createChecklist(ChecklistCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Checklist payload is required");
        }

        ChecklistCategory category = checklistCategoryRepository.findById(request.getChecklistCategoryId())
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist category not found: " + request.getChecklistCategoryId()));

        checklistRepository
                .findByTitleAndUserIdAndChecklistCategoryId(request.getTitle(), request.getUserId(), category.getId())
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .ifPresent(existing -> {
                    throw new BadRequestException("Checklist already exists for this user and category");
                });

        Checklist checklist = new Checklist();
        checklist.setTitle(request.getTitle());
        checklist.setDescription(request.getDescription());
        checklist.setChecklistCategory(category);
        checklist.setUserId(request.getUserId());
        checklist.setStatus(request.getStatus() == null ? ChecklistStatus.PENDING : request.getStatus());
        checklist.setReferenceLink(request.getReferenceLink());
        checklist.setCompletedAt(request.getCompletedAt());
        checklist.setIsDeleted(false);

        String createdBy = request.getCreatedBy();
        if (createdBy == null || createdBy.trim().isEmpty()) {
            createdBy = "system";
        }
        checklist.setCreatedBy(createdBy);
        checklist.setCreatedDate(LocalDateTime.now());

        Checklist saved = checklistRepository.save(checklist);
        log.info("Created checklist id={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    @Override
    public List<ChecklistResponse> getAllChecklists() {
        List<Checklist> checklists = checklistRepository.findByIsDeletedFalse();
        log.info("Fetched {} checklists", checklists.size());
        return checklists.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ChecklistResponse getChecklistById(Long id) {
        Checklist checklist = checklistRepository.findById(id)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist not found: " + id));
        log.info("Fetched checklist id={}", id);
        return toResponse(checklist);
    }

    @Override
    public ChecklistResponse updateChecklist(Long id, ChecklistUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Checklist payload is required");
        }

        Checklist existing = checklistRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist not found: " + id));

        ChecklistCategory category = existing.getChecklistCategory();
        if (request.getChecklistCategoryId() != null) {
            category = checklistCategoryRepository.findById(request.getChecklistCategoryId())
                    .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Checklist category not found: " + request.getChecklistCategoryId()));
        }

        String newTitle = request.getTitle() != null ? request.getTitle() : existing.getTitle();
        Long newUserId = request.getUserId() != null ? request.getUserId() : existing.getUserId();
        Long newCategoryId = category != null ? category.getId() : existing.getChecklistCategory().getId();

        checklistRepository.findByTitleAndUserIdAndChecklistCategoryId(newTitle, newUserId, newCategoryId)
                .filter(found -> !found.getId().equals(existing.getId()))
                .filter(found -> Boolean.FALSE.equals(found.getIsDeleted()))
                .ifPresent(found -> {
                    throw new BadRequestException("Checklist already exists for this user and category");
                });

        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getChecklistCategoryId() != null) {
            existing.setChecklistCategory(category);
        }
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getReferenceLink() != null) {
            existing.setReferenceLink(request.getReferenceLink());
        }
        if (request.getCompletedAt() != null) {
            existing.setCompletedAt(request.getCompletedAt());
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        Checklist saved = checklistRepository.save(existing);
        log.info("Updated checklist id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteChecklist(Long id) {
        Checklist existing = checklistRepository.findById(id)
                .filter(value -> Boolean.FALSE.equals(value.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Checklist not found: " + id));

        existing.setIsDeleted(true);
        existing.setModifiedBy("system");
        existing.setModifiedDate(LocalDateTime.now());
        checklistRepository.save(existing);
        log.info("Soft deleted checklist id={}", id);
    }

    private ChecklistResponse toResponse(Checklist checklist) {
        Long categoryId = checklist.getChecklistCategory() != null ? checklist.getChecklistCategory().getId() : null;
        return ChecklistResponse.builder()
                .id(checklist.getId())
                .title(checklist.getTitle())
                .description(checklist.getDescription())
                .checklistCategoryId(categoryId)
                .userId(checklist.getUserId())
                .status(checklist.getStatus())
                .referenceLink(checklist.getReferenceLink())
                .completedAt(checklist.getCompletedAt())
                .createdBy(checklist.getCreatedBy())
                .createdDate(checklist.getCreatedDate())
                .modifiedBy(checklist.getModifiedBy())
                .modifiedDate(checklist.getModifiedDate())
                .build();
    }
}
