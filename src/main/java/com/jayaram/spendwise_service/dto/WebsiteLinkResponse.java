package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebsiteLinkResponse {

    private Long id;
    private String websiteLink;
    private String description;
    private String remarks;
    private Boolean isActive;
    private Long categoryId;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
