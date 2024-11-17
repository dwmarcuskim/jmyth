package com.marcus.jmyth;

import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Configuration
public class AssistantHeaderConfiguration {
    @Bean
    public ClientHttpRequestInterceptor requestInterceptor(OpenAIProperties openAIProperties) {
        return new ClientHttpRequestInterceptor() {
            @Nonnull
            @Override
            public ClientHttpResponse intercept(@Nonnull HttpRequest request, @Nonnull byte[] body, @Nonnull ClientHttpRequestExecution execution) throws IOException {
                var response = execution.execute(request, body);
                response.getHeaders().add("Authorization", "Bearer " + openAIProperties.getApiKey());
                response.getHeaders().add("OpenAI-Beta", "assistants=v1");
                return response;
            }
        };
    }
}