package com.jayaram.spendwise_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Long userId;
    private Long permissionId;
    private String status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
