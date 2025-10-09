package com.example.SanjiBE.controller;

import com.example.SanjiBE.service.LocalOriginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "DB Save", description = "CSV 파일 DB 저장용")
@RequiredArgsConstructor
public class LocalOriginController {

    private final LocalOriginService localService;

    @PostMapping("/import-local")
    @Operation(summary = "국산품 원산지 저장")
    public String importLocalOriginData() throws Exception {
        localService.importCsvToDb();
        return "saved local_origin.csv in DB";
    }
}
