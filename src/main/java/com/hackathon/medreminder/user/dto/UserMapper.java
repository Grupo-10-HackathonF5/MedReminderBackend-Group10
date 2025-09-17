package com.hackathon.medreminder.user.dto;

import com.hackathon.medreminder.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User user);
}
