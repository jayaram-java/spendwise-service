package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import com.jayaram.spendwise_service.util.ChecklistStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistCreateRequest {

    @NotBlank(message = "is required")
    private String title;

    private String description;

    @NotNull(message = "is required")
    private Long checklistCategoryId;

    @NotNull(message = "is required")
    private Long userId;

    private ChecklistStatus status;

    private String referenceLink;

    private LocalDateTime completedAt;

    private String createdBy;
}
