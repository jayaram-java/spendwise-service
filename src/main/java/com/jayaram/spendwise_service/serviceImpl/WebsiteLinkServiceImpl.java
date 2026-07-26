package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jayaram.spendwise_service.dto.WebsiteLinkCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteLinkResponse;
import com.jayaram.spendwise_service.dto.WebsiteLinkUpdateRequest;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.WebsiteCategory;
import com.jayaram.spendwise_service.model.WebsiteLinkDetails;
import com.jayaram.spendwise_service.repository.WebsiteCategoryRepository;
import com.jayaram.spendwise_service.repository.WebsiteLinkRepository;
import com.jayaram.spendwise_service.service.WebsiteLinkService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebsiteLinkServiceImpl implements WebsiteLinkService {

    private final WebsiteLinkRepository websiteLinkRepository;
    private final WebsiteCategoryRepository websiteCategoryRepository;

    @Override
    public WebsiteLinkResponse createWebsiteLink(WebsiteLinkCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Website link payload is required");
        }

        WebsiteCategory category = resolveCategory(request.getCategoryId());

        WebsiteLinkDetails link = WebsiteLinkDetails.builder()
                .websiteLink(request.getWebsiteLink())
                .description(request.getDescription())
                .remarks(request.getRemarks())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .category(category)
                .build();

        String createdBy = request.getCreatedBy();
        if (createdBy == null || createdBy.trim().isEmpty()) {
            createdBy = "system";
        }
        link.setCreatedBy(createdBy);
        link.setCreatedDate(LocalDateTime.now());

        WebsiteLinkDetails saved = websiteLinkRepository.save(link);
        log.info("Created website link id={} categoryId={}", saved.getId(),
                saved.getCategory() == null ? null : saved.getCategory().getId());
        return toResponse(saved);
    }

    @Override
    public List<WebsiteLinkResponse> getAllWebsiteLinks() {
        List<WebsiteLinkDetails> links = websiteLinkRepository.findAll();
        log.info("Fetched {} website links", links.size());
        return links.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public WebsiteLinkResponse getWebsiteLinkById(Long id) {
        WebsiteLinkDetails link = websiteLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website link not found: " + id));
        log.info("Fetched website link id={}", id);
        return toResponse(link);
    }

    @Override
    public WebsiteLinkResponse updateWebsiteLink(Long id, WebsiteLinkUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Website link payload is required");
        }

        WebsiteLinkDetails existing = websiteLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website link not found: " + id));

        if (request.getWebsiteLink() != null) {
            existing.setWebsiteLink(request.getWebsiteLink());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getRemarks() != null) {
            existing.setRemarks(request.getRemarks());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        if (request.getCategoryId() != null) {
            WebsiteCategory category = resolveCategory(request.getCategoryId());
            existing.setCategory(category);
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        WebsiteLinkDetails saved = websiteLinkRepository.save(existing);
        log.info("Updated website link id={}", id);
        return toResponse(saved);
    }

    @Override
    public void deleteWebsiteLink(Long id) {
        WebsiteLinkDetails existing = websiteLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Website link not found: " + id));
        websiteLinkRepository.delete(existing);
        log.info("Deleted website link id={}", id);
    }

    private WebsiteCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BadRequestException("Category id is required");
        }

        return websiteCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Website category not found: " + categoryId));
    }

    private WebsiteLinkResponse toResponse(WebsiteLinkDetails link) {
        return WebsiteLinkResponse.builder()
                .id(link.getId())
                .websiteLink(link.getWebsiteLink())
                .description(link.getDescription())
                .remarks(link.getRemarks())
                .isActive(link.getIsActive())
                .categoryId(link.getCategory() != null ? link.getCategory().getId() : null)
                .createdBy(link.getCreatedBy())
                .createdDate(link.getCreatedDate())
                .modifiedBy(link.getModifiedBy())
                .modifiedDate(link.getModifiedDate())
                .build();
    }
}
