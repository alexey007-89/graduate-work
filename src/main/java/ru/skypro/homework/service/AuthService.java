package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegReq;
import ru.skypro.homework.dto.RoleDTO;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(RegReq regReq, RoleDTO role);
}
