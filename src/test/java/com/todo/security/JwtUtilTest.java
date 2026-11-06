package com.todo.security;

import com.todo.security.jwt.JwtProperties;
import com.todo.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "unit-test-secret-key-with-at-least-32-chars";
    private static final String OTHER_SECRET = "another-unit-test-secret-key-with-32-chars";

    private final JwtUtil jwtUtil = new JwtUtil(new JwtProperties(SECRET, Duration.ofHours(1)));

    @Test
    void generateToken_ShouldProduceTokenThatValidatesAndKeepsUsername() {
        String token = jwtUtil.generateToken("alice");

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenSignedWithDifferentSecret() {
        JwtUtil otherUtil = new JwtUtil(new JwtProperties(OTHER_SECRET, Duration.ofHours(1)));

        assertFalse(jwtUtil.validateToken(otherUtil.generateToken("alice")));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenExpired() {
        JwtUtil shortLivedUtil = new JwtUtil(new JwtProperties(SECRET, Duration.ofMillis(-1)));

        assertFalse(jwtUtil.validateToken(shortLivedUtil.generateToken("alice")));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenMalformed() {
        assertFalse(jwtUtil.validateToken("not-a-jwt"));
    }
}
