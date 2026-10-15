package com.jayaram.spendwise_service.service;

import java.util.List;

import com.jayaram.spendwise_service.dto.ExpenseDetailCreateRequest;
import com.jayaram.spendwise_service.dto.ExpenseDetailResponse;
import com.jayaram.spendwise_service.dto.ExpenseReceiptResponse;
import com.jayaram.spendwise_service.dto.ExpenseDetailUpdateRequest;
import com.jayaram.spendwise_service.storage.ReceiptFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ExpenseDetailService {

    ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request);

    ExpenseDetailResponse createExpenseDetail(ExpenseDetailCreateRequest request, List<MultipartFile> receipts);

    List<ExpenseDetailResponse> getAllExpenseDetails();

    ExpenseDetailResponse getExpenseDetailById(Long id);

    ExpenseDetailResponse updateExpenseDetail(Long id, ExpenseDetailUpdateRequest request);

    List<ExpenseReceiptResponse> getExpenseReceipts(Long expenseId);

    ReceiptFileResponse getExpenseReceiptFile(Long expenseId, Long receiptId);

    void deleteExpenseDetail(Long id);
}
