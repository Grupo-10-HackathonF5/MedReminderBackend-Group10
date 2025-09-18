package com.hackathon.medreminder.shared;

import com.hackathon.medreminder.auth.TokenBlacklistService;
import com.hackathon.medreminder.shared.security.RestAuthenticationEntryPoint;
import com.hackathon.medreminder.shared.security.jwt.JwtAuthFilter;
import com.hackathon.medreminder.shared.security.jwt.JwtService;
import com.hackathon.medreminder.user.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_callsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_malformedAuthorizationHeader_callsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("BadHeader value");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_invalidToken_invokesAuthenticationEntryPoint() throws Exception {
        String token = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(restAuthenticationEntryPoint).commence(eq(request), eq(response), any(AuthenticationCredentialsNotFoundException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_blacklistedToken_invokesAuthenticationEntryPoint() throws Exception {
        String token = "blacklistedToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(tokenBlacklistService.isTokenInBlacklist(token)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(restAuthenticationEntryPoint).commence(eq(request), eq(response), any(AuthenticationCredentialsNotFoundException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_validToken_setsAuthenticationAndProceeds() throws Exception {
        String token = "validToken";
        String username = "someUser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(tokenBlacklistService.isTokenInBlacklist(token)).thenReturn(false);
        when(jwtService.extractUsername(token)).thenReturn(username);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(userService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_authenticationException_invokesEntryPoint() throws Exception {
        String token = "tokenCausingException";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenThrow(new AuthenticationCredentialsNotFoundException("Invalid token"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(restAuthenticationEntryPoint).commence(eq(request), eq(response), any(AuthenticationCredentialsNotFoundException.class));
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
