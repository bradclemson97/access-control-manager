package com.example.accesscontrolmanager.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Slf4j
public class KmClientConfig {

    @Value("${keycloak-manager.url:http://localhost:8210}")
    private String kmUrl;

    @Bean
    public KmClient kmClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl(kmUrl)
                .requestInterceptor((request, body, execution) -> {
                    try {
                        var attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                        String authHeader = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                        if (authHeader != null) {
                            request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);
                        }
                    } catch (IllegalStateException ignored) {
                    }
                    return execution.execute(request, body);
                })
                .build();
        log.info("Registered KmClient → {}", kmUrl);
        return new KmClient(restClient);
    }
}
