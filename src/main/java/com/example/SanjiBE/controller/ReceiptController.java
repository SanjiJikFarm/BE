package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ReceiptRequest;
import com.example.SanjiBE.dto.ReceiptResponse;
import com.example.SanjiBE.dto.ReceiptSummaryResponse;
import com.example.SanjiBE.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
@Tag(name = "Receipt", description = "영수증 저장 및 가져오기 API")
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<ReceiptResponse> saveReceipt(
            @RequestParam String username,
            @RequestBody ReceiptRequest request) {
        ReceiptResponse response = receiptService.saveReceipt(username, request);
        return ResponseEntity.ok(response);
    }

    // 저장 없이 요약만
    @Operation(summary = "영수증 요약(비저장)", description = "OCR 결과를 받아 매장명, 날짜, 총액만 반환")
    @PostMapping("/summary")
    public ResponseEntity<ReceiptSummaryResponse> summarize(@RequestBody ReceiptRequest request) {
        return ResponseEntity.ok(ReceiptSummaryResponse.from(request));
    }

    // 저장된 영수증 id로 요약
    @GetMapping("/{receiptId}/summary")
    public ResponseEntity<ReceiptSummaryResponse> summarizeById(@PathVariable Long receiptId) {
        return ResponseEntity.ok(receiptService.getSummaryById(receiptId));
    }

}
