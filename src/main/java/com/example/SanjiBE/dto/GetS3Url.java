package com.example.SanjiBE.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetS3Url {
    private String presignedUrl;
    private String key;

    @Builder
    public GetS3Url(String key, String presignedUrl) {
        this.key = key;
        this.presignedUrl = presignedUrl;
    }
}
