package com.jayaram.spendwise_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteCategoryUpdateRequest {

    private String categoryName;

    private String description;

    private Boolean isActive;

    private String modifiedBy;
}
