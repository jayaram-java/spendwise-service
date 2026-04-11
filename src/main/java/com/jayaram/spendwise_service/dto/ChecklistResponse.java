package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import com.jayaram.spendwise_service.util.ChecklistStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistResponse {

    private Long id;
    private String title;
    private String description;
    private Long checklistCategoryId;
    private Long userId;
    private ChecklistStatus status;
    private String referenceLink;
    private LocalDateTime completedAt;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
