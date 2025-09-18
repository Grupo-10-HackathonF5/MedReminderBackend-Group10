package com.hackathon.medreminder.auth.dto;

import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    User toUser(RegisterRequest registerRequest, Role role);

    RegisterResponse toRegisterResponse(User user, String message);
}


