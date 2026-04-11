package com.jayaram.spendwise_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayaram.spendwise_service.dto.ChecklistCreateRequest;
import com.jayaram.spendwise_service.dto.ChecklistResponse;
import com.jayaram.spendwise_service.dto.ChecklistUpdateRequest;
import com.jayaram.spendwise_service.service.ChecklistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/checklists")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping
    public ResponseEntity<ChecklistResponse> createChecklist(@Valid @RequestBody ChecklistCreateRequest request) {
        log.info("Create checklist request received");
        ChecklistResponse created = checklistService.createChecklist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ChecklistResponse>> getAllChecklists() {
        log.info("Get all checklists request received");
        return ResponseEntity.ok(checklistService.getAllChecklists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistResponse> getChecklistById(@PathVariable Long id) {
        log.info("Get checklist by id request received id={}", id);
        return ResponseEntity.ok(checklistService.getChecklistById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChecklistResponse> updateChecklist(@PathVariable Long id,
            @Valid @RequestBody ChecklistUpdateRequest request) {
        log.info("Update checklist request received id={}", id);
        return ResponseEntity.ok(checklistService.updateChecklist(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long id) {
        log.info("Delete checklist request received id={}", id);
        checklistService.deleteChecklist(id);
        return ResponseEntity.noContent().build();
    }
}
