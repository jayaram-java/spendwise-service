package com.jayaram.spendwise_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteLinkCreateRequest {

    @NotBlank(message = "is required")
    private String websiteLink;

    private String description;

    private String remarks;

    private Boolean isActive;

    @NotNull(message = "is required")
    private Long categoryId;

    private String createdBy;
}
