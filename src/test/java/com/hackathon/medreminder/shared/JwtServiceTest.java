package com.hackathon.medreminder.shared;

import com.hackathon.medreminder.shared.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secretKey = "VGhpcyBpcyBhIHZlcnkgc2VjcmV0IGtleSBmb3IgdGVzdGluZyE=";
    private final long expirationMs = 1000 * 60 * 60; // 1 hour

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, expirationMs);

        testUser = new User(
                "testuser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void generateAndValidateAccessToken() {
        String accessToken = jwtService.generateAccessToken(testUser);
        assertNotNull(accessToken);
        assertTrue(jwtService.isValidToken(accessToken));
        assertEquals("testuser", jwtService.extractUsername(accessToken));
    }

    @Test
    void generateAndValidateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testUser);
        assertNotNull(refreshToken);
        assertTrue(jwtService.isValidToken(refreshToken));
        assertEquals("testuser", jwtService.extractUsername(refreshToken));
    }

    @Test
    void extractClaim_returnsCorrectValue() {
        String token = jwtService.generateAccessToken(testUser);
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("testuser", subject);
    }

    @Test
    void extractExpiration_returnsFutureDate() {
        String token = jwtService.generateAccessToken(testUser);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_returnsFalseForValidToken() {
        String token = jwtService.generateAccessToken(testUser);
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void isValidToken_returnsFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.isValidToken(invalidToken));
    }

    @Test
    void refreshAccessToken_succeedsForValidRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testUser);
        String newAccessToken = jwtService.refreshAccessToken(refreshToken, testUser);
        assertNotNull(newAccessToken);
        assertNotEquals(refreshToken, newAccessToken);
        assertEquals(testUser.getUsername(), jwtService.extractUsername(newAccessToken));
    }

    @Test
    void refreshAccessToken_throwsForInvalidRefreshToken() {
        String invalidRefreshToken = "invalid.token";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            jwtService.refreshAccessToken(invalidRefreshToken, testUser);
        });
        assertTrue(exception.getMessage().contains("Invalid or expired refresh token"));
    }
}
