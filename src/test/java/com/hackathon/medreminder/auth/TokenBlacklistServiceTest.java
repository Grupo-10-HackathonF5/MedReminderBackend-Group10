package com.hackathon.medreminder.auth;

import com.hackathon.medreminder.shared.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenBlacklistServiceTest {

    @Mock
    private JwtService jwtService;

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenBlacklistService = new TokenBlacklistService(jwtService);
    }

    @Test
    void addToBlacklist_withValidExpiration() {
        String token = "valid-token";
        Date expirationDate = new Date(System.currentTimeMillis() + 10000); // 10 seconds from now

        when(jwtService.extractExpiration(token)).thenReturn(expirationDate);

        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isTokenInBlacklist(token));
    }

    @Test
    void addToBlacklist_withException_setsDefaultExpiration() {
        String token = "invalid-token";

        when(jwtService.extractExpiration(token)).thenThrow(new RuntimeException("Invalid token"));

        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isTokenInBlacklist(token));
    }

    @Test
    void isTokenInBlacklist_tokenNotPresent() {
        assertFalse(tokenBlacklistService.isTokenInBlacklist("unknown-token"));
    }

    @Test
    void isTokenInBlacklist_expiredTokenRemovesIt() throws InterruptedException {
        String token = "expiring-token";
        tokenBlacklistService.getBlacklistedTokens().put(token, System.currentTimeMillis() + 100);

        assertTrue(tokenBlacklistService.isTokenInBlacklist(token));

        Thread.sleep(150);

        assertFalse(tokenBlacklistService.isTokenInBlacklist(token));
        assertFalse(tokenBlacklistService.getBlacklistedTokens().containsKey(token));
    }
}
