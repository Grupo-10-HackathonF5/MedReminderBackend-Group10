package com.SleepUp.SU.user.user;

import com.SleepUp.SU.user.User;
import com.SleepUp.SU.user.UserRepository;
import com.SleepUp.SU.user.dto.UserMapper;
import com.SleepUp.SU.user.dto.UserRequest;
import com.SleepUp.SU.user.dto.UserResponse;
import com.SleepUp.SU.user.utils.UserServiceHelper;
import com.SleepUp.SU.utils.EntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityUtil mapperUtil;
    private final UserServiceHelper userServiceHelper;

    public UserResponse getLoggedUser(Long id){
        return userMapper.toResponse(userServiceHelper.findById(id));
    }

    public UserResponse updateLoggedUser(UserRequest userRequest, Long id){
        User user = userServiceHelper.findById(id);
        userServiceHelper.updateUserData(userRequest, user);
        return (userMapper.toResponse(user));
    }

    public void deleteMyUser(Long id){
        userRepository.deleteById(userServiceHelper.findById(id).getId());
    }
}
