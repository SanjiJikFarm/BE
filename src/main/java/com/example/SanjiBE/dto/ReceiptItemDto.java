package com.example.SanjiBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ReceiptItemDto {
    private String name;
    private String price;
    private String qty;
    private String total;
}
