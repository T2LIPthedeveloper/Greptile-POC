package com.mcpgateway.proxy;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {

    private static final String PREFIX = "mcp:session:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redis;

    public SessionManager(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String createSession(String upstreamUrl) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        redis.opsForValue().set(PREFIX + sessionId, upstreamUrl, TTL);
        return sessionId;
    }

    public boolean isValid(String sessionId) {
        return sessionId != null && Boolean.TRUE.equals(redis.hasKey(PREFIX + sessionId));
    }

    public void terminate(String sessionId) {
        if (sessionId != null) {
            redis.delete(PREFIX + sessionId);
        }
    }
}
