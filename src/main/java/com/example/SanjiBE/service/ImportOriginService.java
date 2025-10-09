package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.ImportOrigin;
import com.example.SanjiBE.repository.ImportOriginRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ImportOriginService {

    private final ImportOriginRepository importRepo;

    public void importCsvToDb() throws IOException {
        // 기존 데이터 초기화
        importRepo.deleteAll();

        // resources 폴더에 있는 import_origin.csv 파일 읽기
        File file = new ClassPathResource("import_origin.csv").getFile();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            // 최신 버전에서는 CSVFormat.Builder 사용
            CSVFormat format = CSVFormat.Builder.create()
                    .setHeader() // 첫 줄을 헤더로 사용
                    .setSkipHeaderRecord(true) // 첫 줄을 데이터로 처리하지 않음
                    .build();

            try (CSVParser parser = new CSVParser(reader, format)) {
                for (CSVRecord record : parser) {
                    String item = record.get("품목명").trim();
                    String country = record.get("대표국가").trim();

                    importRepo.save(new ImportOrigin(item, country));
                }
            }
        }
    }
}
