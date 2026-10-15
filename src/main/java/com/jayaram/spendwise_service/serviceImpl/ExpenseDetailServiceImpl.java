package com.jayaram.spendwise_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.jayaram.spendwise_service.dto.ExpenseDetailCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseDetailUpdateRequest;
import com.jayaram.spendwise_service.dto.ExpenseReceiptResponse;
import com.jayaram.spendwise_service.exception.BadRequestException;
import com.jayaram.spendwise_service.exception.ResourceNotFoundException;
import com.jayaram.spendwise_service.model.ExpenseCategory;
import com.jayaram.spendwise_service.model.ExpenseDetail;
import com.jayaram.spendwise_service.model.ExpenseReceipt;
import com.jayaram.spendwise_service.repository.ExpenseCategoryRepository;
import com.jayaram.spendwise_service.repository.ExpenseDetailRepository;
import com.jayaram.spendwise_service.repository.ExpenseReceiptRepository;
import com.jayaram.spendwise_service.security.UserPrincipal;
import com.jayaram.spendwise_service.service.ExpenseDetailService;
import com.jayaram.spendwise_service.storage.ReceiptFileResponse;
import com.jayaram.spendwise_service.storage.ReceiptStorageService;
import com.jayaram.spendwise_service.storage.StoredReceipt;
import com.jayaram.spendwise_service.util.ExpenseCodeGenerator;
import com.jayaram.spendwise_service.validation.ReceiptFileValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseDetailServiceImpl implements ExpenseDetailService {

    private final ExpenseDetailRepository expenseDetailRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCodeGenerator expenseCodeGenerator;
    private final ExpenseReceiptRepository expenseReceiptRepository;
    private final ReceiptFileValidator receiptFileValidator;
    private final ReceiptStorageService receiptStorageService;

    @Override
    @Transactional
    public ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request) {
        return createExpenseDetail(request, null);
    }

    @Override
    @Transactional
    public ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request, List<MultipartFile> receipts) {
        if (request == null) {
            throw new BadRequestException("Expense detail payload is required");
        }

        receiptFileValidator.validateAll(receipts);
        List<MultipartFile> receiptFiles = receipts == null ? List.of() : receipts;

        ExpenseCategory category = resolveCategory(request.getCategoryId());
        Long userId = expenseCodeGenerator.getCurrentUserId();
        String expenseCode = expenseCodeGenerator.generateNextExpenseCode(userId, request.getExpenseDate());

        ExpenseDetail detail = ExpenseDetail.builder()
                .expenseName(request.getExpenseName())
                .expenseDate(request.getExpenseDate())
                .amount(request.getAmount())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .expenseCode(expenseCode)
                .referenceNumber(request.getReferenceNumber())
                .receiptUrl(request.getReceiptUrl())
                .currency(request.getCurrency() == null ? "INR" : request.getCurrency())
                .userId(userId)
                .status(request.getStatus() == null ? "ACTIVE" : request.getStatus())
                .isDeleted(false)
                .category(category)
                .build();

        if (request.getCreatedBy() == null || request.getCreatedBy().trim().isEmpty()) {
            detail.setCreatedBy("system");
        } else {
            detail.setCreatedBy(request.getCreatedBy());
        }
        detail.setCreatedDate(LocalDateTime.now());

        ExpenseDetail saved = expenseDetailRepository.save(detail);
        List<String> storedPaths = new ArrayList<>();
        registerRollbackCleanup(storedPaths);

        try {
            List<ExpenseReceipt> receiptMetadata = new ArrayList<>();
            for (MultipartFile receiptFile : receiptFiles) {
                StoredReceipt storedReceipt = receiptStorageService.store(saved.getId(), receiptFile);
                storedPaths.add(storedReceipt.storagePath());

                ExpenseReceipt receipt = ExpenseReceipt.builder()
                        .expenseDetail(saved)
                        .originalFileName(storedReceipt.originalFileName())
                        .storedFileName(storedReceipt.storedFileName())
                        .storagePath(storedReceipt.storagePath())
                        .contentType(storedReceipt.contentType())
                        .fileSizeBytes(storedReceipt.fileSizeBytes())
                        .build();
                receipt.setCreatedBy(saved.getCreatedBy());
                receipt.setCreatedDate(LocalDateTime.now());
                receiptMetadata.add(receipt);
            }

            if (!receiptMetadata.isEmpty()) {
                expenseReceiptRepository.saveAll(receiptMetadata);
            }

            log.info("Created expense detail id={} userId={} receiptCount={}",
                    saved.getId(), saved.getUserId(), receiptMetadata.size());
            return toResponse(saved);
        } catch (RuntimeException ex) {
            cleanupStoredReceipts(storedPaths);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDetailResponse> getAllExpenseDetails() {
        Long userId = getAuthenticatedUserId();
        List<ExpenseDetail> details = expenseDetailRepository.findByUserIdAndIsDeletedFalse(userId);
        log.info("Fetched {} expense details", details.size());
        return details.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDetailResponse getExpenseDetailById(Long id) {
        ExpenseDetail detail = findActiveExpense(id);
        log.info("Fetched expense detail id={}", id);
        return toResponse(detail);
    }

    @Override
    @Transactional
    public ExpenseDetailResponse updateExpenseDetail(Long id, ExpenseDetailUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Expense detail payload is required");
        }

        ExpenseDetail existing = findActiveExpense(id);

        if (request.getExpenseName() != null) {
            existing.setExpenseName(request.getExpenseName());
        }
        if (request.getExpenseDate() != null) {
            existing.setExpenseDate(request.getExpenseDate());
        }
        if (request.getAmount() != null) {
            existing.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getPaymentMethod() != null) {
            existing.setPaymentMethod(request.getPaymentMethod());
        }
        if (request.getExpenseCode() != null) {
            existing.setExpenseCode(request.getExpenseCode());
        }
        if (request.getReferenceNumber() != null) {
            existing.setReferenceNumber(request.getReferenceNumber());
        }
        if (request.getReceiptUrl() != null) {
            existing.setReceiptUrl(request.getReceiptUrl());
        }
        if (request.getCurrency() != null) {
            existing.setCurrency(request.getCurrency());
        }
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }

        if (request.getCategoryId() != null) {
            ExpenseCategory category = resolveCategory(request.getCategoryId());
            existing.setCategory(category);
        }

        String modifiedBy = request.getModifiedBy();
        if (modifiedBy == null || modifiedBy.trim().isEmpty()) {
            modifiedBy = "system";
        }
        existing.setModifiedBy(modifiedBy);
        existing.setModifiedDate(LocalDateTime.now());

        ExpenseDetail saved = expenseDetailRepository.save(existing);
        log.info("Updated expense detail id={}", id);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseReceiptResponse> getExpenseReceipts(Long expenseId) {
        ExpenseDetail detail = findActiveExpense(expenseId);
        authorizeReceiptAccess(detail);
        return expenseReceiptRepository.findAllByExpenseDetail_Id(expenseId).stream()
                .map(receipt -> toReceiptResponse(expenseId, receipt))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptFileResponse getExpenseReceiptFile(Long expenseId, Long receiptId) {
        ExpenseDetail detail = findActiveExpense(expenseId);
        authorizeReceiptAccess(detail);

        ExpenseReceipt receipt = expenseReceiptRepository.findByIdAndExpenseDetail_Id(receiptId, expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found: " + receiptId));
        Resource resource = receiptStorageService.load(receipt.getStoragePath());
        return new ReceiptFileResponse(resource, receipt.getOriginalFileName(), receipt.getContentType());
    }

    @Override
    @Transactional
    public void deleteExpenseDetail(Long id) {
        ExpenseDetail existing = findActiveExpense(id);

        existing.setIsDeleted(true);
        existing.setModifiedBy("system");
        existing.setModifiedDate(LocalDateTime.now());
        expenseDetailRepository.save(existing);
        log.info("Soft deleted expense detail id={}", id);
    }

    private ExpenseCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BadRequestException("Category id is required");
        }

        return expenseCategoryRepository.findById(categoryId)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found: " + categoryId));
    }

    private ExpenseDetail findActiveExpense(Long id) {
        return expenseDetailRepository.findById(id)
                .filter(existing -> Boolean.FALSE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Expense detail not found: " + id));
    }

    private ExpenseDetailResponse toResponse(ExpenseDetail detail) {
        List<ExpenseReceiptResponse> receipts = expenseReceiptRepository.findAllByExpenseDetail_Id(detail.getId()).stream()
                .map(receipt -> toReceiptResponse(detail.getId(), receipt))
                .collect(Collectors.toList());

        return ExpenseDetailResponse.builder()
                .id(detail.getId())
                .expenseName(detail.getExpenseName())
                .expenseDate(detail.getExpenseDate())
                .amount(detail.getAmount())
                .description(detail.getDescription())
                .paymentMethod(detail.getPaymentMethod())
                .expenseCode(detail.getExpenseCode())
                .referenceNumber(detail.getReferenceNumber())
                .receiptUrl(detail.getReceiptUrl())
                .receipts(receipts)
                .currency(detail.getCurrency())
                .userId(detail.getUserId())
                .status(detail.getStatus())
                .categoryId(detail.getCategory() != null ? detail.getCategory().getId() : null)
                .createdBy(detail.getCreatedBy())
                .createdDate(detail.getCreatedDate())
                .modifiedBy(detail.getModifiedBy())
                .modifiedDate(detail.getModifiedDate())
                .build();
    }

    private ExpenseReceiptResponse toReceiptResponse(Long expenseId, ExpenseReceipt receipt) {
        return ExpenseReceiptResponse.builder()
                .id(receipt.getId())
                .fileName(receipt.getOriginalFileName())
                .contentType(receipt.getContentType())
                .fileSize(receipt.getFileSizeBytes())
                .viewUrl("/api/v1/expense-details/" + expenseId + "/receipts/" + receipt.getId())
                .build();
    }

    private void authorizeReceiptAccess(ExpenseDetail expenseDetail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            return;
        }

        Long currentUserId = getAuthenticatedUserId();
        if (!Objects.equals(currentUserId, expenseDetail.getUserId())) {
            throw new AccessDeniedException("You are not allowed to access these receipts");
        }
    }

    private void registerRollbackCleanup(List<String> storedPaths) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    cleanupStoredReceipts(storedPaths);
                }
            }
        });
    }

    private void cleanupStoredReceipts(List<String> storedPaths) {
        for (int index = storedPaths.size() - 1; index >= 0; index--) {
            receiptStorageService.delete(storedPaths.get(index));
        }
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Authenticated user is required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal && userPrincipal.getUserId() != null) {
            return userPrincipal.getUserId();
        }
        if (principal instanceof Number number) {
            return number.longValue();
        }
        if (principal instanceof String principalValue && !principalValue.isBlank()) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ex) {
                throw new BadRequestException("Authenticated user id is invalid");
            }
        }

        throw new BadRequestException("Authenticated user id is missing");
    }
}
