package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.ChecklistCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistResponse;
import com.jayaram.spendwise_service.dto.ChecklistUpdateRequest;

public interface ChecklistService {

    ChecklistResponse createChecklist(ChecklistCreateRequest request);

    List<ChecklistResponse> getAllChecklists();

    ChecklistResponse getChecklistById(Long id);

    ChecklistResponse updateChecklist(Long id, ChecklistUpdateRequest request);

    void deleteChecklist(Long id);
}
