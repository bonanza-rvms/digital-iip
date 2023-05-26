package com.bonanza.sjin.config.enums;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bithumb.api")
public class BithumbConfig {
    @NotBlank(message = "Secret Key를 설정해주세요.")
    private String secretKey;

    @NotBlank(message = "Access Key를 설정해주세요.")
    private String accessKey;

    @NotBlank(message = "API서버 URL을 설정해주세요.")
    private String apiUrl;
    
    @NotBlank(message = "웹소켓 URL을 설정해주세요.")
    private String wssUrl;
}
