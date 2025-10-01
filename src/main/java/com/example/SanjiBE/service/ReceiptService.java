package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReceiptRequest;
import com.example.SanjiBE.dto.ReceiptResponse;
import com.example.SanjiBE.dto.ReceiptSummaryResponse;

public interface ReceiptService {
    ReceiptResponse saveReceipt(String username, ReceiptRequest req);

    ReceiptSummaryResponse getSummaryById(Long receiptId);
}
