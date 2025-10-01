package com.example.SanjiBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ReceiptResponse {
    private long receiptId;
    private String storeName;
    private String date;
    private int totalAmount;
    private List<ReceiptItemDto> itemList;
}
