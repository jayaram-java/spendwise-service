package com.jayaram.spendwise_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.WebsiteCategory;

import java.util.Optional;

public interface WebsiteCategoryRepository  extends JpaRepository<WebsiteCategory, Long> {

    Optional<WebsiteCategory> findByCategoryNameIgnoreCase(String categoryName);

}
