package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.LoginReq;
import ru.skypro.homework.dto.RegReq;
import ru.skypro.homework.dto.RoleDTO;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import javax.validation.Valid;

import static ru.skypro.homework.dto.RoleDTO.USER;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthServiceImpl authService;

    @Operation(
            tags = "Авторизация (AuthController)",
            summary = "Вход (login)"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReq req) {
        if (authService.login(req.getUsername(), req.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            tags = "Авторизация (AuthController)",
            summary = "Регистрация (register)"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegReq req) {
        RoleDTO role = req.getRoleDTO() == null ? USER : req.getRoleDTO();
        if (authService.register(req, role)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
