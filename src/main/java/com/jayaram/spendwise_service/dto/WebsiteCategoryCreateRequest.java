package com.jayaram.spendwise_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteCategoryCreateRequest {

    @NotBlank(message = "is required")
    private String categoryName;

    private String description;

    private Boolean isActive;

    private String createdBy;
}
