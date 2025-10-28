package com.example.SanjiBE.service;

import org.springframework.stereotype.Service;

/**
 * OpenAI 연동용 더미 서비스 (AI 서버 미연동시 기본 응답 반환)
 * 실제 GPT 호출은 SanjiAI 서버에서 처리됨.
 */
@Service
public class OpenAiService {

    public String ask(String prompt) {
        // 단순한 기본 응답 (AI 서버 없을 경우 안전한 fallback)
        if (prompt.contains("운송수단")) {
            return "트럭";
        } else if (prompt.contains("생산지")) {
            return "국내";
        }
        return "국내";
    }
}
