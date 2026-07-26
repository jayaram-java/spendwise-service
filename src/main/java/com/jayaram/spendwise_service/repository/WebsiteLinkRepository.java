package com.jayaram.spendwise_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jayaram.spendwise_service.model.WebsiteLinkDetails;

public interface WebsiteLinkRepository extends JpaRepository<WebsiteLinkDetails, Long> {

}
