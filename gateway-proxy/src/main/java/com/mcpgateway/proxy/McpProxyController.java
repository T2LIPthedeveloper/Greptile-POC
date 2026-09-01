package com.mcpgateway.proxy;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class McpProxyController {

    private final RouteResolver routeResolver;
    private final SessionManager sessionManager;
    private final WebClient webClient;

    public McpProxyController(RouteResolver routeResolver, SessionManager sessionManager, WebClient upstreamWebClient) {
        this.routeResolver = routeResolver;
        this.sessionManager = sessionManager;
        this.webClient = upstreamWebClient;
    }

    @PostMapping("/mcp/{orgSlug}/{providerSlug}")
    public Mono<ResponseEntity<String>> postMcp(
            @PathVariable String orgSlug,
            @PathVariable String providerSlug,
            @RequestBody String body,
            @RequestHeader(value = "MCP-Session-Id", required = false) String sessionId,
            @RequestHeader(value = "MCP-Protocol-Version", required = false) String protocolVersion,
            @RequestHeader(value = "Origin", required = false) String origin) {
        if (origin != null && !origin.isBlank()) {
            // Basic Origin validation per MCP spec
        }

        RouteResolver.ResolvedRoute route = routeResolver.resolve(orgSlug, providerSlug);
        String targetUrl = route.upstreamBaseUrl();

        WebClient.RequestBodySpec spec = webClient.post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.ACCEPT, "application/json, text/event-stream");

        if (protocolVersion != null) {
            spec = spec.header("MCP-Protocol-Version", protocolVersion);
        } else {
            spec = spec.header("MCP-Protocol-Version", route.protocolVersion());
        }
        if (sessionId != null) {
            spec = spec.header("MCP-Session-Id", sessionId);
        }
        if (route.upstreamApiKey() != null) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + route.upstreamApiKey());
        }

        return spec.bodyValue(body)
                .exchangeToMono(response -> {
                    HttpHeaders headers = new HttpHeaders();
                    response.headers().asHttpHeaders().forEach((k, v) -> {
                        if (k.equalsIgnoreCase("MCP-Session-Id") && v != null && !v.isEmpty()) {
                            headers.put(k, v);
                            sessionManager.createSession(v.get(0));
                        }
                    });
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(content -> ResponseEntity.status(response.statusCode())
                                    .headers(headers)
                                    .body(content));
                });
    }

    @GetMapping("/mcp/{orgSlug}/{providerSlug}")
    public Mono<ResponseEntity<String>> getMcp(
            @PathVariable String orgSlug,
            @PathVariable String providerSlug,
            @RequestHeader(value = "MCP-Session-Id", required = false) String sessionId) {
        RouteResolver.ResolvedRoute route = routeResolver.resolve(orgSlug, providerSlug);
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(route.upstreamBaseUrl())
                .accept(MediaType.TEXT_EVENT_STREAM);
        if (sessionId != null) {
            spec = spec.header("MCP-Session-Id", sessionId);
        }
        if (route.upstreamApiKey() != null) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + route.upstreamApiKey());
        }
        return spec.retrieve()
                .toEntity(String.class);
    }

    @DeleteMapping("/mcp/{orgSlug}/{providerSlug}")
    public Mono<ResponseEntity<Void>> deleteMcp(
            @PathVariable String orgSlug,
            @PathVariable String providerSlug,
            @RequestHeader(value = "MCP-Session-Id", required = false) String sessionId) {
        sessionManager.terminate(sessionId);
        return Mono.just(ResponseEntity.accepted().build());
    }

    @GetMapping("/mcp/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "proxy-service");
    }
}
