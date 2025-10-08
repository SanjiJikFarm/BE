package com.example.SanjiBE.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRequest {
    private String storeName;
    private String date;
    private String totalAmount;

    @JsonProperty("items")
    private List<ReceiptItemDto> itemList;
}
