package com.example.carRental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {
    private Long idUsers;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long idRoles;
}
