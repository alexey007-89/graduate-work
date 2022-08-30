package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class RegReq {
    private String firstName;
    private String lastName;
    private String password;
    private RoleDTO roleDTO;
    private String phone;
    private String username;
}
