package com.hackathon.medreminder.auth.dto;

import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.role.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User toUser(RegisterRequest registerRequest, Role role);

    RegisterResponse toRegisterResponse(User user, String message);

}

