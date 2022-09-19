package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegReq {
    @NotBlank
    @Size(min = 3)
    private String firstName;
    @NotBlank
    @Size(min = 3)
    private String lastName;
    @Size(min = 8)
    private String password;
    private RoleDTO roleDTO;
    private String phone;
    @Email
    private String username;
}
