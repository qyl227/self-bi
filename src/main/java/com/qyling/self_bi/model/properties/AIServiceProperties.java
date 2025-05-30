package com.qyling.self_bi.model.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qyling
 * @date 2025/5/29 14:40
 */
@Component
@ConfigurationProperties(prefix = "my-ai")
@Data
public class AIServiceProperties {
    private String model;
    private String secretKey;
    private String apiUrl;
}
