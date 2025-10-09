package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.LocalOrigin;
import com.example.SanjiBE.repository.LocalOriginRepository;
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
public class LocalOriginService {

    private final LocalOriginRepository localRepo;

    public void importCsvToDb() throws IOException {
        // 기존 데이터 초기화
        localRepo.deleteAll();

        // resources 폴더의 local_origin.csv 읽기
        File file = new ClassPathResource("local_origin.csv").getFile();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.Builder.create()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            try (CSVParser parser = new CSVParser(reader, format)) {
                for (CSVRecord record : parser) {
                    String item = record.get("품목명").trim();
                    String region = record.get("주산지").trim();

                    localRepo.save(new LocalOrigin(item, region));
                }
            }
        }
    }
}
