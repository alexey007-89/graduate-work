package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.RegReq;
import ru.skypro.homework.dto.RoleDTO;

import java.security.Principal;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(RegReq regReq, RoleDTO role);

    boolean setPassword(NewPassword newPassword, Principal principal);
}
