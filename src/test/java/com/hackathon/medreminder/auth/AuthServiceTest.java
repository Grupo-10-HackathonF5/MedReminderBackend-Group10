package com.hackathon.medreminder.auth;

import com.hackathon.medreminder.auth.dto.*;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import com.hackathon.medreminder.shared.security.jwt.JwtService;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.role.Role;
import com.hackathon.medreminder.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User unsavedUser;
    private User savedUser;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest("Alice", "Smith", "alice123", "alice@example.com", "password123");
        unsavedUser = new User();
        savedUser = new User();
        registerResponse = new RegisterResponse(123, "Alice", "alice123", "Smith", "alice@example.com", "User successfully registered");
    }

    @Test
    void register_success() {
        when(authMapper.toUser(registerRequest, Role.USER)).thenReturn(unsavedUser);
        when(userService.createUser(unsavedUser)).thenReturn(savedUser);
        when(authMapper.toRegisterResponse(savedUser, "User successfully registered")).thenReturn(registerResponse);

        RegisterResponse result = authService.register(registerRequest);

        assertEquals(registerResponse, result);
        verify(userService).createUser(unsavedUser);
    }

    @Test
    void login_success() {
        LoginRequest loginRequest = new LoginRequest("username", "password");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        AuthResponse response = authService.login(loginRequest);

        assertEquals("login", response.message());
        assertEquals("Bearer", response.tokenType());
        assertEquals("access-token", response.token());
        assertEquals(loginRequest.username(), response.username());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void refresh_success() {
        RefreshRequest refreshRequest = new RefreshRequest("valid-refresh-token");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("username");
        when(tokenBlacklistService.isTokenInBlacklist(refreshRequest.refreshToken())).thenReturn(false);
        when(jwtService.isValidToken(refreshRequest.refreshToken())).thenReturn(true);
        when(jwtService.refreshAccessToken(refreshRequest.refreshToken(), userDetails)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");

        AuthResponse response = authService.refresh(refreshRequest, userDetails);

        assertEquals("refresh", response.message());
        assertEquals("Bearer", response.tokenType());
        assertEquals("new-access-token", response.token());
        assertEquals(userDetails.getUsername(), response.username());
        assertEquals("new-refresh-token", response.refreshToken());

        verify(tokenBlacklistService).addToBlacklist(refreshRequest.refreshToken());
    }

    @Test
    void refresh_throwsWhenTokenBlacklisted() {
        RefreshRequest refreshRequest = new RefreshRequest("blacklisted-token");
        UserDetails userDetails = mock(UserDetails.class);

        when(tokenBlacklistService.isTokenInBlacklist(refreshRequest.refreshToken())).thenReturn(true);

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> authService.refresh(refreshRequest, userDetails));
    }

    @Test
    void logout_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.isValidToken("valid-token")).thenReturn(true);
        when(request.getHeader("Refresh-Token")).thenReturn("valid-refresh-token");
        when(jwtService.isValidToken("valid-refresh-token")).thenReturn(true);

        ApiMessage message = authService.logout(request);

        assertEquals("Logout successful", message.message());
        verify(tokenBlacklistService).addToBlacklist("valid-token");
        verify(tokenBlacklistService).addToBlacklist("valid-refresh-token");
    }

    @Test
    void logout_throwsWhenNoBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> authService.logout(request));
    }
}

