package com.jayaram.spendwise_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistCategoryUpdateRequest {

    @Size(max = 100, message = "must be at most 100 characters")
    private String name;

    private String description;

    private Boolean isActive;

    private Boolean isPrimary;

    private Long userId;

    private String modifiedBy;
}
