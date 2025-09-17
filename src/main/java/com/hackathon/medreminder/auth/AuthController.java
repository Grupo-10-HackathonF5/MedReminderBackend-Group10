package com.hackathon.medreminder.auth;

import com.hackathon.medreminder.auth.dto.*;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import com.hackathon.medreminder.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request)  {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return authService.refresh(request, customUserDetails);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessage logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
