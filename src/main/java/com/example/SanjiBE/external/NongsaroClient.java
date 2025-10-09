package com.example.SanjiBE.external;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.SanjiBE.dto.FoodApiResponse;

@Component
public class NongsaroClient {

    @Value("${nongsaro.api.base-url}")
    private String baseUrl;

    @Value("${nongsaro.api.key}")
    private String apiKey;

    /**
     * 농사로 API 호출 - 월별 식재료 목록 조회
     */
    public List<FoodApiResponse> fetchMonthFood(int year, int month) {
        try {
            String y = String.valueOf(year);
            String m = String.format("%02d", month);

            String url = baseUrl
                    + "/monthFdmtLst"
                    + "?apiKey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                    + "&thisYear=" + URLEncoder.encode(y, StandardCharsets.UTF_8)
                    + "&thisMonth=" + URLEncoder.encode(m, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() / 100 != 2) {
                throw new RuntimeException("농사로 API 실패: HTTP " + res.statusCode());
            }

            return parseXml(res.body());

        } catch (Exception e) {
            throw new RuntimeException("농사로 API 호출 오류: " + e.getMessage(), e);
        }
    }

    /**
     * XML 파싱 → FoodApiResponse 리스트로 변환
     */
    private List<FoodApiResponse> parseXml(String xml) throws Exception {
        var list = new ArrayList<FoodApiResponse>();

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        var doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String foodNum = text(item, "cntntsNo");
            String foodName = text(item, "fdmtNm");
            String cours = text(item, "rtnFileCours");
            String fileNm = text(item, "rtnStreFileNm");

            String imageUrl = toImageUrl(cours, fileNm);

            if (foodNum != null && foodName != null) {
                list.add(new FoodApiResponse(foodNum, foodName, imageUrl));
            }
        }

        return list;
    }

    /**
     * 특정 태그 내용을 텍스트로 추출
     */
    private static String text(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) {
            return null;
        }
        Node n = nl.item(0);
        String s = n.getTextContent();
        return (s == null) ? null : s.trim();
    }

    /**
     * 파일 경로 + 파일명 조합 → 이미지 URL 생성
     */
    private static String toImageUrl(String path, String name) {
        if (path == null || name == null) {
            return null;
        }

        // 슬래시 정리
        String p = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        String f = name.startsWith("/") ? name.substring(1) : name;

        // 확장자 확인 (jpg/png 등만 허용)
        if (!f.contains(".")) {
            return null;
        }

        // 절대경로 또는 상대경로에 따라 URL 생성
        if (p.startsWith("http://") || p.startsWith("https://")) {
            return p + "/" + f;
        } else {
            // HTTPS로 변경
            return "https://www.nongsaro.go.kr" + (p.startsWith("/") ? "" : "/") + p + "/" + f;
        }
    }
}
