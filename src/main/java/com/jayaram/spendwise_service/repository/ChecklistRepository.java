package com.jayaram.spendwise_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.Checklist;
import com.jayaram.spendwise_service.util.ChecklistStatus;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findByIsDeletedFalse();

    Optional<Checklist> findByTitleAndUserIdAndChecklistCategoryId(String title, Long userId, Long checklistCategoryId);

    List<Checklist> findByUserId(Long userId);

    List<Checklist> findByChecklistCategoryId(Long categoryId);

    List<Checklist> findByStatus(ChecklistStatus status);
}
