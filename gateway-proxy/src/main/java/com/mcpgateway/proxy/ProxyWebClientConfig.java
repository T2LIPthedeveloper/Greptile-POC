package com.mcpgateway.proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProxyWebClientConfig {

    @Bean
    public WebClient upstreamWebClient() {
        return WebClient.builder().build();
    }
}
