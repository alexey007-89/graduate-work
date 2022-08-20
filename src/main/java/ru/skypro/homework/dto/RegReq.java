package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class RegReq {
    private String password;
    private RoleDTO roleDTO;
    private String username;
}
