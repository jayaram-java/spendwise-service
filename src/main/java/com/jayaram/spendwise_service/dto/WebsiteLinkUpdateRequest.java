package com.jayaram.spendwise_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteLinkUpdateRequest {

    private String websiteLink;

    private String description;

    private String remarks;

    private Boolean isActive;

    private Long categoryId;

    private String modifiedBy;
}
