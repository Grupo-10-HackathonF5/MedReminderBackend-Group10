package com.hackathon.medreminder.auth;

import com.hackathon.medreminder.auth.dto.*;
import com.hackathon.medreminder.shared.security.jwt.JwtService;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.role.Role;
import com.hackathon.medreminder.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthMapper authMapper;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        User unSaved = authMapper.toUser(request, Role.USER);
        User savedUser = userService.createUser(unSaved);
        return authMapper.toRegisterResponse(savedUser, "User successfully registered");
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse("login", "Bearer", accessToken, loginRequest.username(), refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request, UserDetails userDetails) {
        if (tokenBlacklistService.isTokenInBlacklist(request.refreshToken())) {
            throw new AuthenticationCredentialsNotFoundException("Refresh token is blacklisted");
        }
        if (!jwtService.isValidToken(request.refreshToken())) {
            throw new AuthenticationCredentialsNotFoundException("Invalid or expired refresh token");
        }
        tokenBlacklistService.addToBlacklist(request.refreshToken());

        String newAccessToken = jwtService.refreshAccessToken(request.refreshToken(), userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse("refresh", "Bearer", newAccessToken, userDetails.getUsername(), newRefreshToken);
    }

    public ApiMessage logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationCredentialsNotFoundException("No Bearer token found in Authorization header");
        }

        String token = authorizationHeader.substring(7);
        if (jwtService.isValidToken(token)) {
            tokenBlacklistService.addToBlacklist(token);
        }

        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken != null && jwtService.isValidToken(refreshToken)) {
            tokenBlacklistService.addToBlacklist(refreshToken);
        }

        return new ApiMessage("Logout successful");
    }

}

