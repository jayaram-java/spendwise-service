package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.WebsiteLinkCreateRequest;
import com.jayaram.spendwise_service.dto.WebsiteLinkResponse;
import com.jayaram.spendwise_service.dto.WebsiteLinkUpdateRequest;

public interface WebsiteLinkService {

    WebsiteLinkResponse createWebsiteLink(WebsiteLinkCreateRequest request);

    List<WebsiteLinkResponse> getAllWebsiteLinks();

    WebsiteLinkResponse getWebsiteLinkById(Long id);

    WebsiteLinkResponse updateWebsiteLink(Long id, WebsiteLinkUpdateRequest request);

    void deleteWebsiteLink(Long id);
}
