package com.example.SanjiBE.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ReceiptRequest {
    private String storeName;
    private String date;
    private String totalAmount;
    private List<ReceiptItemDto> itemList;

    public ReceiptItemDto[] getItems() {
        return itemList.toArray(new ReceiptItemDto[0]);
    }
}

