package com.example.SanjiBE.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ReceiptItemDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long productId;

    private String name;
    private String price;
    private String qty;
    private String total;
}
