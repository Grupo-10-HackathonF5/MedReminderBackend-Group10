package com.SleepUp.SU.user.user;

import com.SleepUp.SU.user.CustomUserDetails;
import com.SleepUp.SU.user.dto.UserRequest;
import com.SleepUp.SU.user.dto.UserResponse;
import com.SleepUp.SU.utils.ApiMessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserUserController {

    private final UserUserService userUserService;

    @GetMapping("/my-user")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getLoggedUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return userUserService.getLoggedUser(customUserDetails.getId());
    }

    @PutMapping("/my-user")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse putLoggedUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestBody @Valid UserRequest userRequest){
        return userUserService.updateLoggedUser(userRequest, customUserDetails.getId());
    }

    @DeleteMapping("/my-user")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessageDto deleteLoggedUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        userUserService.deleteMyUser(customUserDetails.getId());
        return new ApiMessageDto("Account deleted!!");
    }
}
