package com.hackathon.medreminder.auth;

import com.hackathon.medreminder.shared.security.jwt.JwtService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Getter
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    private final JwtService jwtService;

    public void addToBlacklist(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);
            blacklistedTokens.put(token, expiration.getTime());
        } catch (Exception e){
            long expirationTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
            blacklistedTokens.put(token, expirationTime);
        }
    }

    public boolean isTokenInBlacklist(String token) {
        Long expirationTime = blacklistedTokens.get(token);

        if (expirationTime == null){
            return false;
        }

        if (System.currentTimeMillis() > expirationTime) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }
}