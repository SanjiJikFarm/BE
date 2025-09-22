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

  // API 호출 메서드
  public List<FoodApiResponse> fetchMonthFood(int year, int month) {
    try {
      String y = String.valueOf(year);
      String m = String.format("%02d", month);
      String url = baseUrl // URL 인코딩
          + "/monthFdmtLst"
          + "?apiKey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
          + "&thisYear=" + URLEncoder.encode(y, StandardCharsets.UTF_8)
          + "&thisMonth=" + URLEncoder.encode(m, StandardCharsets.UTF_8);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
      HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

      // 성공
      if(res.statusCode() / 100 != 2 ){
        throw new RuntimeException("농사로 API 실패: HTTP" + res.statusCode());
      } 

      return parseXml(res.body());  

    } catch (Exception e) {
      throw new RuntimeException("농사로 API 호출 오류" + e.getMessage(), e);
    }
  }

  // XML 파싱
  private List<FoodApiResponse> parseXml(String xml) throws Exception{
    var list = new ArrayList<FoodApiResponse>();
    
    var dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(false);
    var doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

    // item 태그 추출
    NodeList items = doc.getElementsByTagName("item");

    // Item tag 추출 후에 DTO에 추가
    for(int i = 0; i < items.getLength(); i++){
      Element item = (Element) items.item(i);

      String foodNum = text(item, "cntntsNo");
      String foodName = text(item, "fdmtNm");
      String cours = text(item, "rtnFileCours");
      String fileNm = text(item, "rtnStreFileNm");

      // 경로 + 파일이름 -> URL
      String imageUrl = toImageUrl(cours, fileNm);

      if(foodNum != null && foodName != null){
        list.add(new FoodApiResponse(foodNum, foodName, imageUrl));
      }
    }
    return list;
  }
  // tag를 text로
  private static String text(Element parent, String tag){
    NodeList nl = parent.getElementsByTagName(tag);
    if(nl.getLength() == 0){
      return null;
    }

    Node n = nl.item(0);
    String s= n.getTextContent();
    return (s == null) ? null : s.trim();
  }

  // 슬래시 제거후 주소 변환
  private static String toImageUrl(String path, String name){
    if(path == null || name == null){
      return null;
    }

    // 슬래시 중복 방지 로직
    String p = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    String f = name.startsWith("/") ? path.substring(1) : path;

    if (p.startsWith("http://") || p.startsWith("https://")) {
      return p + "/" + f;
    } else {
        return "http://www.nongsaro.go.kr" + (p.startsWith("/") ? "" : "/") + p + "/" + f;
    }
  }
}