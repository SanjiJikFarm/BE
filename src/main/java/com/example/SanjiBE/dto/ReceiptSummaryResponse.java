package com.example.SanjiBE.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptSummaryResponse {
    private String storeName;
    private String date;
    private int totalAmount;

    public static int parseAmount(String s) {
        if (s == null) return 0;
        return Integer.parseInt(s.replace(",", "").trim());
    }

    public static ReceiptSummaryResponse from(ReceiptRequest req) {
        return ReceiptSummaryResponse.builder()
                .storeName(req.getStoreName())
                .date(req.getDate())
                .totalAmount(parseAmount(req.getTotalAmount()))
                .build();
    }
}
