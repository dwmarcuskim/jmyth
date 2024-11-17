package com.marcus.jmyth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantFeignClientConfiguration {
    @Bean
    public AssistantFeignClient assistantFeignClientInterceptor(OpenAIProperties openAIProperties) {
        return new AssistantFeignClient(null, null, openAIProperties);
    }
}
