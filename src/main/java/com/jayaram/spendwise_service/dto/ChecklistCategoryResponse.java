package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Boolean isPrimary;
    private Long userId;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
