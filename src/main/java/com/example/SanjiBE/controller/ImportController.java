package com.example.SanjiBE.controller;

import com.example.SanjiBE.service.ImportOriginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "DB Save", description = "CSV 파일 DB 저장용")
@RequiredArgsConstructor
public class ImportController {

    private final ImportOriginService importService;

    // 한 번만 호출하면 CSV → DB로 저장됨
    @PostMapping("/import-origin")
    @Operation(summary = "수입품 원산지 저장")
    public String importOriginData() throws Exception {
        importService.importCsvToDb();
        return "saved import_origin.csv in DB";
    }
}
