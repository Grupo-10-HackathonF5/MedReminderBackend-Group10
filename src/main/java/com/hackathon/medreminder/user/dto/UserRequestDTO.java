package com.hackathon.medreminder.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}