package com.mcpgateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public JwtTokenService(
            @Value("${mcp.security.jwt.secret}") String secret,
            @Value("${mcp.security.jwt.access-token-ttl-seconds:3600}") long accessTokenTtlSeconds,
            @Value("${mcp.security.jwt.refresh-token-ttl-seconds:86400}") long refreshTokenTtlSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public String createAccessToken(AuthenticatedUser user) {
        return buildToken(user, accessTokenTtlSeconds, "access");
    }

    public String createRefreshToken(AuthenticatedUser user) {
        return buildToken(user, refreshTokenTtlSeconds, "refresh");
    }

    private String buildToken(AuthenticatedUser user, long ttlSeconds, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.userId().toString())
                .claim("orgId", user.orgId().toString())
                .claim("email", user.email())
                .claim("roles", user.roles())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(secretKey)
                .compact();
    }

    public AuthenticatedUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String type = claims.get("type", String.class);
        if (!"access".equals(type)) {
            throw new IllegalArgumentException("Invalid token type");
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        return new AuthenticatedUser(
                UUID.fromString(claims.getSubject()),
                UUID.fromString(claims.get("orgId", String.class)),
                claims.get("email", String.class),
                roles);
    }

    public AuthenticatedUser parseRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid token type");
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        return new AuthenticatedUser(
                UUID.fromString(claims.getSubject()),
                UUID.fromString(claims.get("orgId", String.class)),
                claims.get("email", String.class),
                roles);
    }
}
