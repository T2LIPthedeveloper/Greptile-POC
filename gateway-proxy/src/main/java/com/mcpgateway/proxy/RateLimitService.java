package com.mcpgateway.proxy;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RateLimitService {

    private static final String PREFIX = "mcp:ratelimit:";
    private static final int MAX_REQUESTS_PER_MINUTE = 120;

    private final StringRedisTemplate redis;

    public RateLimitService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void check(String key) {
        String redisKey = PREFIX + key;
        Long count = redis.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redis.expire(redisKey, Duration.ofMinutes(1));
        }
        if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }
}
