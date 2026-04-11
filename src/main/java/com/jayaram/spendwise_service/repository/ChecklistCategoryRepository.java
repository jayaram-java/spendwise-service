package com.jayaram.spendwise_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.ChecklistCategory;

public interface ChecklistCategoryRepository extends JpaRepository<ChecklistCategory, Long> {

    List<ChecklistCategory> findByIsDeletedFalse();

    List<ChecklistCategory> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<ChecklistCategory> findByNameAndUserId(String name, Long userId);
}
