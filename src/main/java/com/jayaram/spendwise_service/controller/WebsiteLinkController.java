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

import com.jayaram.spendwise_service.dto.WebsiteLinkCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteLinkResponse;
import com.jayaram.spendwise_service.dto.WebsiteLinkUpdateRequest;
import com.jayaram.spendwise_service.service.WebsiteLinkService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/website-links")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
@Slf4j
public class WebsiteLinkController {

    private final WebsiteLinkService websiteLinkService;

    @PostMapping
    public ResponseEntity<WebsiteLinkResponse> createWebsiteLink(
            @Valid @RequestBody WebsiteLinkCreateRequest request) {
        log.info("Create website link request received");
        WebsiteLinkResponse created = websiteLinkService.createWebsiteLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<WebsiteLinkResponse>> getAllWebsiteLinks() {
        log.info("Get all website links request received");
        return ResponseEntity.ok(websiteLinkService.getAllWebsiteLinks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebsiteLinkResponse> getWebsiteLinkById(@PathVariable Long id) {
        log.info("Get website link by id request received id={}", id);
        return ResponseEntity.ok(websiteLinkService.getWebsiteLinkById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebsiteLinkResponse> updateWebsiteLink(@PathVariable Long id,
            @Valid @RequestBody WebsiteLinkUpdateRequest request) {
        log.info("Update website link request received id={}", id);
        return ResponseEntity.ok(websiteLinkService.updateWebsiteLink(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebsiteLink(@PathVariable Long id) {
        log.info("Delete website link request received id={}", id);
        websiteLinkService.deleteWebsiteLink(id);
        return ResponseEntity.noContent().build();
    }
}
