package com.jayaram.spendwise_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseCategoryUpdateRequest {

    @Size(max = 100, message = "must be at most 100 characters")
    private String name;

    private String description;

    private Long userId;

    private Long permissionId;

    private String status;

    private String modifiedBy;
}
