package ru.skypro.homework.service;

import ru.skypro.homework.dto.ResponseWrapperUser;
import ru.skypro.homework.dto.UserDto;

import java.security.Principal;

public interface UserService {

    ResponseWrapperUser getUsers();

    UserDto updateUser(UserDto userDto, Principal principal);

    UserDto getUser(int id);
}
