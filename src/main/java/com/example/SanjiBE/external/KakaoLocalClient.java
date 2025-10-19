// 파일: com.example.SanjiBE.external.KakaoLocalClient.java
package com.example.SanjiBE.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * 카카오 로컬(주소→좌표) 클라이언트 - HttpClient 버전.
 * 프로젝트의 NongsaroClient와 동일한 스택으로 통일.
 */
@Component
public class KakaoLocalClient {

    @Value("${kakao.local.base-url}")
    private String baseUrl; // 예: https://dapi.kakao.com

    @Value("${kakao.local.api-key}")
    private String apiKey;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public Optional<GeoPoint> geocodeAddress(String address) {
        try {
            if (address == null || address.isBlank()) return Optional.empty();

            String url = baseUrl
                    + "/v2/local/search/address.json"
                    + "?query=" + URLEncoder.encode(address, StandardCharsets.UTF_8);

            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Authorization", "KakaoAK " + apiKey)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() / 100 != 2) {
                throw new RuntimeException("카카오 로컬 API 실패: HTTP " + res.statusCode());
            }

            // 매우 간단한 JSON 파싱(외부 라이브러리 없이)
            // 기대 형태: {"documents":[{"x":"126.97","y":"37.56", ...}, ...], ...}
            String body = res.body();
            int docsIdx = body.indexOf("\"documents\"");
            if (docsIdx < 0) return Optional.empty();
            int firstX = body.indexOf("\"x\"", docsIdx);
            int firstY = body.indexOf("\"y\"", docsIdx);
            if (firstX < 0 || firstY < 0) return Optional.empty();

            String xVal = extractJsonStringValue(body, firstX);
            String yVal = extractJsonStringValue(body, firstY);
            if (xVal == null || yVal == null) return Optional.empty();

            double lng = Double.parseDouble(xVal);
            double lat = Double.parseDouble(yVal);
            return Optional.of(new GeoPoint(lat, lng));
        } catch (Exception e) {
            throw new RuntimeException("카카오 로컬 API 호출/파싱 오류: " + e.getMessage(), e);
        }
    }

    // "x":"126.97" 형태에서 값만 추출하는 간단 파서
    private static String extractJsonStringValue(String json, int keyPos) {
        int colon = json.indexOf(':', keyPos);
        if (colon < 0) return null;
        int quote1 = json.indexOf('"', colon + 1);
        if (quote1 < 0) return null;
        int quote2 = json.indexOf('"', quote1 + 1);
        if (quote2 < 0) return null;
        return json.substring(quote1 + 1, quote2);
    }

    public record GeoPoint(double latitude, double longitude) {}
}
