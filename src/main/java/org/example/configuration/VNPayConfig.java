package org.example.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    private String payUrl;
    private String apiUrl;
    private String tmnCode;
    private String hashSecret;
    private String version;
    private String command;
} 