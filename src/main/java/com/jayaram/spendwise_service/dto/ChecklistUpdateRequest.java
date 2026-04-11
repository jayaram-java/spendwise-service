package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import com.jayaram.spendwise_service.util.ChecklistStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistUpdateRequest {

    private String title;

    private String description;

    private Long checklistCategoryId;

    private Long userId;

    private ChecklistStatus status;

    private String referenceLink;

    private LocalDateTime completedAt;

    private String modifiedBy;
}
