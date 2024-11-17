package com.marcus.jmyth;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class AssistantFeignClient extends Client.Default {
    private final OpenAIProperties openAIProperties;

    public AssistantFeignClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier, OpenAIProperties openAIProperties) {
        super(sslContextFactory, hostnameVerifier);
        this.openAIProperties = openAIProperties;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        request.header("Authorization", List.of("Bearer " + openAIProperties.getApiKey()));
        request.header("OpenAI-Beta", List.of("assistants=v2"));
        try (var response = super.execute(request, options)) {
            var body = response.body();
            if (body != null) {
                try (InputStream is = body.asInputStream()) {
                    var bodyString = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
                    log.info("Response body: {}", bodyString);
                    return Response.builder()
                            .request(request)
                            .status(response.status())
                            .reason(response.reason())
                            .headers(new HashMap<>(response.headers()))
                            .body(bodyString, StandardCharsets.UTF_8)
                            .build();
                }
            }
            return response;

        }
    }
}
