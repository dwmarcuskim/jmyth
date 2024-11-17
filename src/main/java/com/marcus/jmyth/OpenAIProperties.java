package com.marcus.jmyth;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {
    @Value("${openai.api-key}")
    private String apiKey;
    @Value("${openai.assistant-id}")
    private String assistantId;
}